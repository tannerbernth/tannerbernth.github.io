//window.addEventListener("load", jsontest);

var player = {
	name 	: 	""	,
	level 	: 	1	,
	exp		: 	0	,
	exp_req	:	10	,
	class	: 	{}	,
	hp 		: 	0	,
	mp 		: 	0	,
	eqp		: 	{}	,	
	inv		:	{}	

};

var player_class = {
	archer 		: 	{
		name	: 	"Archer"	,
		agi		: 	4			,
		con		:	3			,
		dex		: 	8			,
		int		:	0			,
		lck		:	5			,
		str		:	0			,
		wis		:	0			,
		tot 	: 	20				
	},
	mage 		: 	{
		name 	: 	"Mage"		,
		agi		: 	4			,
		con		:	2			,
		dex		: 	0			,
		int		:	8			,
		lck		:	0			,
		str		:	0			,
		wis		:	6			,
		tot 	: 	20
	},
	warrior 	: 	{
		name 	: 	"Warrior"	,
		agi		: 	4			,
		con		:	6			,
		dex		: 	0			,
		int		:	0			,
		lck		:	2			,
		str		:	8			,
		wis		:	0			,
		tot		: 	20	
	} 	
};

function init_player(player_name, selected_class) {
	player["name"] = player_name;
	player["class"] = player_class[selected_class];
	player["hp"] = Math.floor((Math.random() * player["class"]["con"]) + (player["class"]["con"]*3));
	player["mp"] = Math.floor((Math.random() * player["class"]["wis"]) + (player["class"]["wis"]*3))
}

function earn_experience() {

}

function level_up() {

}

var output = document.querySelector("#output");
function print_player() {
	output.innerHTML = "player {<br>";
	for (var key in player) {
		
		if (typeof player[key] === "object") {
			console.log("is type object");
			output.innerHTML += key + " {<br>";
			for (var sub_key in player[key]) {
				output.innerHTML += sub_key+": " + player[key][sub_key] + "<br>";
			}
			output.innerHTML += "}<br>";
		} else {
			output.innerHTML += key+": " + player[key] +"<br>";
		}
	}
	output.innerHTML += "}";
}

window.addEventListener("keydown", function(event) {	
	switch (String.fromCharCode(event.keyCode).toLowerCase()) {
		case 'a':
			output.innerHTML = "agi : " + player["class"]["agi"];
			break;
		case 'c':
			output.innerHTML = "con : " + player["class"]["con"];
			break;
		case 'd':
			output.innerHTML = "dex : " + player["class"]["dex"];
			break;
		case 'i':
			output.innerHTML = "int : " + player["class"]["int"];
			break;
		case 'h':
			output.innerHTML = "hp : " + player["hp"];
			break;
		case 'l':
			output.innerHTML = "lck : " + player["class"]["lck"];
			break;
		case 'm':
			output.innerHTML = "mp : " + player["mp"];
			break;
		case 'n':
			output.innerHTML = "class : " + player["class"]["name"];
			break;
		case 'p':
			print_player();
			break;
		case 's':
			output.innerHTML = "str : " + player["class"]["str"];
			break;
		case 'w':
			output.innerHTML = "wis : " + player["class"]["wis"];
			break;
		default:
			//player[String.fromCharCode(event.keyCode).toLowerCase()] = 0 | player[String.fromCharCode(event.keyCode).toLowerCase()]+1;
			//output.innerHTML = String.fromCharCode(event.keyCode).toLowerCase() + " : " + player[String.fromCharCode(event.keyCode).toLowerCase()];
			break;
	}
});

window.addEventListener("click", function(event) {
	var target = event.target;
	var name = document.querySelector("#name").value;
	if (player_class[target.id] == undefined) {
		console.log("undefined class");
		return; 
	} else {
		init_player(name, target.id);
	}
});