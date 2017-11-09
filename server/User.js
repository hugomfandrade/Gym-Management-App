ar azureMobileApps = require('azure-mobile-apps');

// Create a new table definition
var table = azureMobileApps.table();

var crypto = require('crypto');
var iterations = 100;
var bytes = 32;
var aud = "Custom";
var masterKey = "master-key";
var queries = require('azure-mobile-apps/src/query');

var insertMiddleware = function(request, response, next){
	var item = request.body;
	var userTable = request.azureMobile.tables('User');
	
	if (request.query.RequestType === "SignUp") {
		
		query = queries.create('User').where({ Username : item.Username });
		userTable.read(query).then(function(results) {
			if (results.length > 0) {
				response.status(400).send("Username already exists");
				return;
			}
			// Add your own validation - what fields do you require to 
			// add a unique salt to the item
			item.Salt = new Buffer(crypto.randomBytes(bytes)).toString('base64');
			// hash the password
			hash(item.Password, item.Salt, function(err, h) {
				item.Password = h;
			    userTable.insert(item).then(function(results) {
					addCredential(request, item, function(isOk) {
						if (isOk === false) {
							response.status(401).send("ERROR Signing up: Credential not found");
							return;
						} 
						delete item.Password;
						delete item.Salt;
	                    var userId = aud + ":" + item.id;
	                    item.UserID = userId;
	                    var expiry = new Date().setUTCDate(new Date().getUTCDate() + 30);
	                    item.Token = zumoJwt(expiry, aud, userId, masterKey);
				        response.status(200).send({
							UserID: userId,
							Token: zumoJwt(expiry, aud, userId, masterKey),
	                        Username: item.Username
						});
						return;
					});
			    }).catch(function(error) {
					response.status(401).send("Register ERROR: " + error);
					return;
				});
			});
		});  
		return;
	}
	
	var query = queries.create('User').where({ Username : item.Username });
	userTable.read(query).then(function(results) {
		if (results.length === 0) {
			response.status(401).send("Incorrect Username");
			return;
		}
		var user = results[0];
		hash(item.Password, user.Salt, function(err, h) {
			var incoming = h;
			if (!slowEquals(incoming, user.Password)) {
				response.status(401).send("Incorrect password");
				return;
			}
			var expiry = new Date().setUTCDate(new Date().getUTCDate() + 30);
			var userId = aud + ":" + user.id;
			
			response.status(200).send({
				id: user.id,
				UserID: userId,
				Token: zumoJwt(expiry, aud, userId, masterKey),
                Username: user.Username,
                Email: user.Email
			});
		});
	});
};

table.insert.use(insertMiddleware, table.operation);
table.insert(function (context) {
   return context.execute();
});
 
function addCredential(request, item, callback) {
	console.log("Credential_");
	console.log(item);
	// Find Credential
	var waitingUser = request.azureMobile.tables('WaitingUser');
	var waitingUserQuery = queries.create('WaitingUser').where({ Username : item.Username });
	waitingUser.read(waitingUserQuery).then(function(results) {
		if (results.length === 0) {
			callback(false);
			return;
		}
		
		var credential = results[0].Credential;
		
		console.log("Credential");
		console.log(credential);
		// Insert Credential
		var credentialTable = request.azureMobile.tables(credential);
		credentialTable.insert({UserID : item.id})
		.then(function(results) {
			callback(true);
		})
		.catch(function(error) {
			callback(false);
		});
	});
}

function hash(text, salt, callback) {
	crypto.pbkdf2(text, salt, iterations, bytes, function(err, derivedKey){
		if (err) { callback(err); }
		else {
			var h = new Buffer(derivedKey).toString('base64');
			callback(null, h);
		}
	});
}
 
function slowEquals(a, b) {
	var diff = a.length ^ b.length;
    for (var i = 0; i < a.length && i < b.length; i++) {
        diff |= (a[i] ^ b[i]);
	}
    return diff === 0;
}
 
function zumoJwt(expiryDate, aud, userId, masterKey) {
	var crypto = require('crypto');
 
	function base64(input) {
		return new Buffer(input, 'utf8').toString('base64');
	}
 
	function urlFriendly(b64) {
		return b64.replace(/\+/g, '-').replace(/\//g, '_').replace(new RegExp("=", "g"), '');
	}
 
	function signature(input) {
		var key = crypto.createHash('sha256').update(masterKey + "JWTSig").digest('binary');
		var str = crypto.createHmac('sha256', key).update(input).digest('base64');
		return urlFriendly(str);
	}
 
	var s1 = '{"alg":"HS256","typ":"JWT","kid":0}';
	var j2 = {
		"exp":expiryDate.valueOf() / 1000,
		"iss":"urn:microsoft:windows-azure:zumo",
		"ver":1,
		"aud":aud,
		"uid":userId 
	};
	var s2 = JSON.stringify(j2);
	var b1 = urlFriendly(base64(s1));
	var b2 = urlFriendly(base64(s2));
	var b3 = signature(b1 + "." + b2);
	return [b1,b2,b3].join(".");
}

module.exports = table;