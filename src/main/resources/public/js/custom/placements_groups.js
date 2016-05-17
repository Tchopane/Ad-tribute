function loadPage(){
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
	dark.id="overlay";
	
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
	h3divBox.innerHTML = "Select the corresponding groups:";
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

	var divRow2Col3 = document.createElement("div");
	divRow2Col3.className = "col-md-5";
	divRow2.appendChild(divRow2Col3);

	var nextButton = document.createElement("button");
	nextButton.setAttribute("class", "btn-secondary");
	nextButton.onclick = function() {
		
		var nRows = document.getElementById("tableOne").rows.length;

		var jsonData = [];
		for (var i = 1; i < nRows; i++) {
			jsonData.push({
				id : document.getElementById("input" + i).name,
				placement : document.getElementById("thP" + i).innerHTML
						.replace(/&amp;/g, '&'),
				group : document.getElementById("input" + i).value
			});
		}
		postPlacementGroups(jsonData);
	};
	nextButton.innerHTML = "Next";
	divRow2Col3.appendChild(nextButton);
	document.getElementsByTagName('body')[0].appendChild(divRow2);
	
	var divRow3 = document.createElement("p");
	divRow3.className = "instructions";	
	var instructions1 = document.createElement("h4");
	instructions1.textContent = "Please give a group name for each placement. Each Interaction will be placed in the group corresponding to his placement. " +
			"Of course, the names of the groups chosen here have to be different from the names you eventually set in the previous step.";
	divRow3.appendChild(instructions1);
	
	document.getElementsByTagName('body')[0].appendChild(divRow3);

	
}

function ajaxRequest(callback) {
	return $.ajax({
		url : "/PlacementGroup/UniquePlacements",
		type : "GET",
		success : callback
	});
};

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
	var t = document.createTextNode("Placement");
	th.appendChild(t);
	var th = document.createElement('th');
	document.getElementById("tr0").appendChild(th);
	th.id = "th1";
	var u = document.createTextNode("Group");
	th.appendChild(u);

	for (var i = 0; i < json.length; i++) {
		var tr = document.createElement("tr");
		document.getElementById("table_body").appendChild(tr);
		tr.id = "tr" + (i + 1);

		var th = document.createElement('th');
		document.getElementById("tr" + (i + 1)).appendChild(th);
		th.id = "thP" + (i + 1);
		var t = document.createTextNode(json[i]);
		th.appendChild(t);
		if (i == json.length - 1) {
			ajaxRequest2(addGroups);
		}
	}
}

function ajaxRequest2(callback) {
	$.ajax({
		url : "/PlacementGroup/Groups",
		type : "GET",
		success : callback
	});
}

/*$.when(ajaxRequest2()).done(function(output2) {
	addGroups(output2)
});*/

function addGroups(output2) {
	var json = output2;
	for (var i = 0; i < json.length; i++) {
		var th = document.createElement('th');
		document.getElementById("tr" + (i + 1)).appendChild(th);
		th.id = "thG" + (i + 1);
		var input = document.createElement("input");
		input.setAttribute("type", "text");
		input.setAttribute("list", "groups" + (i + 1));
		input.setAttribute("name", json[i].id);
		input.id = "input" + (i + 1);
		input.value = json[i].group;
		th.appendChild(input);
		if (i == json.length - 1) {
			ajaxRequest3(addDataLists);
		}
	}
}

function ajaxRequest3(callback) {
	return $.ajax({
		url : "/PlacementGroup/UniqueGroups",
		type : "GET",
		success : callback
	});
}
 

function addDataLists(output3) {
	var json = output3;
	for (i = 1; i < document.getElementById("tableOne").rows.length; i++) {
		var datalist = document.createElement("datalist");
		datalist.id = "groups" + i;
		for (var j = 0; j < json.length; j++) {
			var option = document.createElement("option");
			option.value = json[j];
			datalist.appendChild(option);
		}
		document.getElementById("thG" + i).appendChild(datalist);
	}
}

function postPlacementGroups(json) {
	$.ajax({
		url : "./PlacementGroup/PostPlacementGroup",
		type : "POST",
		data : JSON.stringify(json),
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		success : function() {
			window.location.href = "activities.html";
		},
		error : function() {
			alert("Posting the table has failed.")
		}
	});
}
