window.addEventListener("load", jsontest);

var jsonchar = {
	name 	: 	""	,
	level 	: 	1	,
	exp		: 	0	,
	exp_req	:	10	,
	class	: 	""	,
	agi		: 	0	,
	con		:	0	,
	dex		: 	0	,
	int		:	0	,
	lck		:	0	,
	str		:	0	,
	wis		:	0	,
	inv		:	{}	

}

function jsontest() {
	var output = document.querySelector("#output");
	window.addEventListener("keyup", function(event) {
		console.log(event.keyCode);
		/*switch (event.key) {
			case 1:
			case 2:
		}*/
	});
}