function loadPage() {
	var loadingSpinner = document.createElement("div");
	loadingSpinner.className = "spinner";
	loadingSpinner.id = "spinner";
	loadingSpinner.style.display = "none";
	var loadingImage = document.createElement("img");
	loadingImage.id = "img-spinner";
	loadingImage.src = "/img/ajax-loader.gif";
	loadingImage.alt = "Loading";
	loadingSpinner.appendChild(loadingImage);

	var dark = document.createElement("div");
	dark.className = "overlay";
	dark.style.display = "none";
	dark.id = "overlay";

	document.getElementsByTagName('body')[0].appendChild(loadingSpinner);
	document.getElementsByTagName('body')[0].appendChild(dark);

	$(document).ajaxSend(function() {
		$("#spinner").show();
		document.getElementById("overlay").style.display = "block";
	});
	$(document).ajaxStop(function() {
		$("#spinner").hide();
		document.getElementById("overlay").style.display = "none";
	});
	$(document).ajaxError(function() {
		$("#spinner").hide();
		document.getElementById("overlay").style.display = "none";
	});
}

function pageContent() {

	var divRow1 = document.createElement("div");
	divRow1.className = "row";
	divRow1.style.margin = "auto";

	var divRow1Col1 = document.createElement("div");
	divRow1Col1.className = "col-md-1";
	divRow1.appendChild(divRow1Col1);

	var divRow1Col2 = document.createElement("div");
	divRow1Col2.className = "col-md-11";
	divRow1.appendChild(divRow1Col2);

	var divBox = document.createElement("div");
	divBox.className = "box";
	divRow1Col2.appendChild(divBox);

	var h3divBox = document.createElement("h3");
	h3divBox.innerHTML = "Select the corresponding states:";
	divBox.appendChild(h3divBox);

	var divDemo = document.createElement("div");
	divDemo.className = "demo";
	divBox.appendChild(divDemo);

	var tableOne = document.createElement("table");
	tableOne.id = "tableOne";
	tableOne.className = "yui";
	divDemo.appendChild(tableOne);

	var tableBody = document.createElement('tbody');
	tableBody.id = "table_body";
	tableOne.appendChild(tableBody);

	document.getElementsByTagName('body')[0].appendChild(divRow1);

	var divRow2 = document.createElement("div");
	divRow2.className = "row";
	divRow2.style.margin = "auto";

	var br = document.createElement("br");
	divRow2.appendChild(br);

	var divRow2Col1 = document.createElement("div");
	divRow2Col1.className = "col-md-1";
	divRow2.appendChild(divRow2Col1);

	var divRow2Col2 = document.createElement("div");
	divRow2Col2.className = "col-md-5";
	divRow2.appendChild(divRow2Col2);

	var previousButton = document.createElement("button");
	previousButton.setAttribute("class", "btn btn-primary");
	previousButton.onclick = function() {
		window.location.href = "placements.html";
	};
	previousButton.innerHTML = "Previous";
	divRow2Col2.appendChild(previousButton);

	var divRow2Col3 = document.createElement("div");
	divRow2Col3.className = "col-md-5";
	divRow2.appendChild(divRow2Col3);

	var nextButton = document.createElement("button");
	nextButton.setAttribute("class", "btn btn-primary");
	nextButton.onclick = function() {
		// Check?
		var nRows = document.getElementById("tableOne").rows.length;
		var jsonData = [];
		for (var i = 1; i < nRows; i++) {
			jsonData.push({
				id : document.getElementById("input" + i).name,
				activity : decodeURIComponent(document.getElementById("thA" + i).textContent),
				state : document.getElementById("input" + i).value
			});
		}
		postActivityStates(jsonData);
	};
	nextButton.innerHTML = "Next";
	nextButton.id = "nextButton";
	nextButton.setAttribute("disabled", "true");
	divRow2Col3.appendChild(nextButton);
	document.getElementsByTagName('body')[0].appendChild(divRow2);
	
	var divRow3 = document.createElement("p");
	divRow3.className = "instructions";
	
	
	var instructions1 = document.createElement("h4");
	instructions1.textContent = "Please assign a state for each activity. You can add new states by using the column on your right. Please sort all the states from the less, "
		+"to the most important one. Once you want to validate this order, just press the 'Set State Order' button.";
	var instructions2 = document.createElement("h4");
	instructions2.textContent = "Notice that you cannot go further as no State order has been set.";
	var blank = document.createElement("br");
	
	divRow3.appendChild(instructions1);
	divRow3.appendChild(blank);
	divRow3.appendChild(instructions2);
	document.getElementsByTagName('body')[0].appendChild(divRow3);

}

function ajaxRequest() {
	return $.ajax({
		url : "/Activities/UniqueActivities",
		type : "GET"
	});
};

$.when(ajaxRequest()).done(function(output1) {
	createTable(output1)
});

function createTable(output1) {
	var json = output1;
	var tbody = document.createElement('tbody');
	document.getElementById("tableOne").appendChild(tbody);
	tbody.id = "table_body";
	// Create headers:
	var tr0 = document.createElement("tr");
	document.getElementById("table_body").appendChild(tr0);
	tr0.id = "tr0";
	var th = document.createElement('th');
	document.getElementById("tr0").appendChild(th);
	th.id = "th0";
	var t = document.createTextNode("Activity");
	th.appendChild(t);
	var th = document.createElement('th');
	document.getElementById("tr0").appendChild(th);
	th.id = "th1";
	var u = document.createTextNode("State");
	th.appendChild(u);

	for (var i = 0; i < json.length; i++) {
		var tr = document.createElement("tr");
		document.getElementById("table_body").appendChild(tr);
		tr.id = "tr" + (i + 1);

		var th = document.createElement('th');
		document.getElementById("tr" + (i + 1)).appendChild(th);
		th.id = "thA" + (i + 1);
		th.innerHTML = json[i];
	}
	ajaxRequest2();
}

function ajaxRequest2() {
	return $.ajax({
		url : "/Activities/States",
		type : "GET"
	});
}

$.when(ajaxRequest2()).done(function(output2) {
	addGroups(output2)
});

function addGroups(output2) {

	var json = output2;

	for (var i = 0; i < json.length; i++) {
		var th = document.createElement('th');
		document.getElementById('tr' + (i + 1)).appendChild(th);
		th.id = "thS" + (i + 1);

		var selector = document.createElement('select');
		selector.setAttribute("class", "form-control selectpicker");
		selector.setAttribute("data-live-search", "true");
		//selector.setAttribute("data-size", "7");
		selector.setAttribute("data-width", "500px");
		selector.id = "input" + (i + 1);
		selector.setAttribute("name", json[i].id);
		selector.setAttribute("title", "Choose a State");
		selector.setAttribute("onchange", "justCheck()");

		th.appendChild(selector);

		//		var input = document.createElement("input");
		//		input.setAttribute("type", "text");
		//		input.setAttribute("list", "groups" + (i + 1));
		//		input.setAttribute("name", json[i].id);
		//		input.id = "input" + (i + 1);
		//		input.value = json[i].state;
		//
		//		th.appendChild(input);
		if (i == json.length - 1) {
			addDataLists();
		}
	}
}

function addDataLists() {
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (xhttp.readyState == 4 && xhttp.status == 200) {
			var json = JSON.parse(xhttp.responseText);
			var selectors = document.getElementsByTagName("select");
			for (k = 0; k < selectors.length; k++) {
				//for (i = 1; i < document.getElementById("tableOne").rows.length; i++) {
				//					var datalist = document.createElement("datalist");
				//					datalist.id = "groups" + i;
				for (var j = 0; j < json.length; j++) {
					var option = document.createElement("option");
					option.value = json[j];
					option.innerHTML = json[j];
					selectors[k].appendChild(option);
				}
				var currentId = selectors[k].id;
				$("#" + currentId).selectpicker('refresh');
				//					document.getElementById("thS" + i).appendChild(datalist);
				//}
			}
		}
	}
	xhttp.open("GET", "/Activities/UniqueStates", true);
	xhttp.send();
}

function postActivityStates(json) {
	$.ajax({
		url : "./Activities/PostActivityState",
		type : "POST",
		data : JSON.stringify(json),
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		success : function() {
			window.location.href = "journeys.html";
		},
		error : function() {
			alert("Posting the table has failed.")
		}
	});
}

function addGroup() {

	var newButton = document.createElement("li");
	newButton.className = "ui-state-default ui-sortable-handle";
	newButton.setAttribute("contenteditable", "true");

	var newName = $('#newName').val();
	if (newName != '') {
		newButton.id = newName;
		newButton.innerHTML = newName;
		document.getElementById("sortable").appendChild(newButton);

		//Add it to the differents selectpickers
		var selectors = document.getElementsByTagName("select");

		for (var k = 0; k < selectors.length; k++) {
			var option = document.createElement("option");
			option.value = newName;
			option.innerHTML = newName;
			selectors[k].appendChild(option);
			var currentId = selectors[k].id;
			$("#" + currentId).selectpicker('refresh');
		}
	}
}

function setStateOrder() {
	var numberOfStates = document.getElementById("sortable").children.length;
	var json = [];
	for (var i = 0; i < numberOfStates; i++) {
		json.push(document.getElementById("sortable").children[i].innerHTML);
	}
	$.ajax({
		url : "./Activities/PostState",
		type : "POST",
		data : JSON.stringify(json),
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		success : function() {
			document.getElementById("nextButton").value = "1";
			justCheck();
		}
	});
}

function justCheck(){
	var selectors = document.getElementsByTagName("select");
	var enable = true;
	for (var i = 0; i < selectors.length; i++) {
		if (selectors[i].value.localeCompare("") === 0) {
			enable = false;
			break;
		}
	}
	if (enable && document.getElementById("nextButton").value.localeCompare("1") === 0) {
		document.getElementById("nextButton").disabled = false;
	}
}
//Cette partie, on va l'appliquer quand on sera a la fin de la page car elle permettra de classer les differents states
//Ce qu'on veut pour le moment, c'est juste ajouter aux selectpickers les nouveaux states qu'on cree via le bouton
//En premier, il faut donc tranformer tous les datalist en selectpicker

//var jsonData = [];
//jsonData.push({
//	name : decodeURIComponent(document.getElementById("thA" + i).textContent),
//	position : document.getElementById("input" + i).value
//});
//
//$.ajax({
//
//})
