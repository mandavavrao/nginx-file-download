function AdminAccountService() {

	var ROUTE = apiRoutePrefixNoSlash() + '/admin/accounts/';

	this.getAll = function(func) {
		$.get(ROUTE, func).fail(showAppModelForJqError);
	}

	this.disable = function(id, func) {
		$.post(ROUTE + id + '/actions/disable', func).fail(showAppModelForJqError);
	}

	this.enable = function(id, func) {
		$.post(ROUTE + id + '/actions/enable', func).fail(showAppModelForJqError);
	}

}
