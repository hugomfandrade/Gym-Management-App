var azureMobileApps = require('azure-mobile-apps');

// Create a new table definition
var table = azureMobileApps.table();

var queries = require('azure-mobile-apps/src/query');

var insertMiddleware = function(request, response, next) {
	var item = request.body;
	
	var user = request.azureMobile.tables('User');
	var userQuery = queries.create('User').where({ Username : item.Username });
	user.read(userQuery).then(function(results) {
		if (results.length > 0) {
			response.status(400).send("Username already exists");
			return;
		}
		
		var waitingUser = request.azureMobile.tables('WaitingUser');
		var waitingUserQuery = queries.create('WaitingUser').where({ Username : item.Username });
		waitingUser.read(waitingUserQuery).then(function(results) {
			if (results.length > 0) {
				response.status(400).send("Username already exists");
				return;
			}
			
			generateCode(10, function(uniqueID) {
				// have a uniqueId
				item.Code = uniqueID;
				waitingUser.insert(item).then(function(results) {
					response.status(200).send(results);
					return;
				})
				.catch(function(error) {
					response.status(401).send("ERROR: " + error);
					return;
				});
			});
			return;
		});
	});
	return;
};

function generateCode(count, callback) {
    var _sym = 'abcdefghijklmnopqrstuvwxyz1234567890';
    var str = '';

    for(var i = 0; i < count; i++) {
        str += _sym[parseInt(Math.random() * (_sym.length))];
    }
	callback(str);
}

table.insert.use(insertMiddleware, table.operation);
table.insert(function (context) {
	return context.execute();
});


module.exports = table;
