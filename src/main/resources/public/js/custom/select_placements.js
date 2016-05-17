function listAllPlacements() {
	$.ajax({
		url : "./DcmApi/ListAllPlacements",
		type : "GET",
		success : function(data) {
			for (key in data) {
				listItem = document.createElement("option");
				listItem.innerHTML = data[key];
				document.getElementById("placementsList").appendChild(listItem);
			}
			$("#placementsList").selectpicker('refresh');
		}
	});
}

function nextButton() {
	if (document.getElementById('q2').style.display == 'block') {
		document.getElementById('q2').style.display = 'none';
	}

	var exist = document.getElementById("button");
	if (exist === null) {
		var divRow2 = document.createElement("div");
		divRow2.className = "row";
		divRow2.style.marginLeft = "75%";

		var nextButton = document.createElement("button");
		nextButton.id = "button";
		nextButton.setAttribute("class", "btn-secondary");
		nextButton.onclick = function() {
			window.location.href = "placements.html";
		};
		nextButton.innerHTML = "Next";
		divRow2.appendChild(nextButton);
		document.getElementsByTagName('body')[0].appendChild(divRow2);
	}
}

function nextButton2(results) {

	var existing = document.getElementById("button");
	var isOk = true;
	for (var i = 0, iLen = results.length; i < iLen; i++) {
		var options = document.getElementById("question3_" + results[i]).options;
		var isOk2 = false;
		for (var j = 0, iLen2 = options.length; j < iLen2; j++) {
			var opt = options[j];

			if (opt.selected && opt.textContent != "Choose one of the following...") {
				isOk2 = true;
				//				ckw.push(opt.value);
				ckw.splice(i, 1);
				ckw.splice(i, 0, parseInt(opt.value));
			}
		}
		isOk = isOk && isOk2;

	}

	if (results.length === 0) {
		isOk = false;
	}

	if (existing === null && isOk && results.length > 0) {
		var divRow2 = document.createElement("div");
		divRow2.id = "buttonDiv";
		divRow2.style.marginLeft = "75%";

		var nextButton = document.createElement("button");
		nextButton.id = "button";
		nextButton.setAttribute("class", "btn-secondary");
		nextButton.onclick = function() {
			var toSplit = results.concat(ckw);
			console.log(toSplit);
			$.ajax({
				url : "./DcmApi/SetGroupingChoices",
				type : "POST",
				data : JSON.stringify(toSplit),
				dataType : "json",
				contentType : "application/json; charset=utf-8",
				success : function() {
					$.ajax({
						url : "./DcmApi/GetGroupingParameters",
						type : "GET",
						success : function(data) {
							if (data == 1) {
								window.location.href = "creatingGroups1.html";
							} else if (data == 2) {
								window.location.href = "creatingGroups2.html";
							} else {
								window.location.href = "creatingGroups3.html";
							}
						}
					})
				}
			});
		};
		nextButton.innerHTML = "Next";
		divRow2.appendChild(nextButton);
		document.getElementsByTagName('body')[0].appendChild(divRow2);
	} else if (existing != null && !isOk) {
		child = document.getElementById("buttonDiv");
		document.getElementsByTagName('body')[0].removeChild(child);
	}
}

function question2(id) {
	if (document.getElementById(id).style.display == 'none') {
		document.getElementById(id).style.display = 'block';
	}
	if (document.getElementById("button")) {
		document.getElementById("button").remove();
	}
}

var placements = [];
var results = [];
var ckw = [];

$(function() {
	$('#selectpicker-container').on('hidden.bs.dropdown', function() {
		//var option = document.getElementById("placementsList").value;
		var options = document.getElementById("placementsList").options;
		var opt;
		for (var i = 0, iLen = options.length; i < iLen; i++) {
			opt = options[i];
			var exist = document.getElementById("question3_" + i);
			if (opt.selected && exist === null) {
				placements.push(opt);
				results.push(i);
				var h3divBox = document.createElement("h3");
				h3divBox.innerHTML = opt.value + " : Do you want to use the campaigns or only the keywords ?";
				h3divBox.id = i;
				document.getElementById("temp").appendChild(h3divBox);

				var selector = document.createElement("select");
				selector.setAttribute("class", "form-control selectpicker");
				selector.setAttribute("data-live-search", "true");
				selector.setAttribute("name", "question3");
				selector.setAttribute("id", "question3_" + i);
				selector.setAttribute("data-size", "3");
				selector.setAttribute("title", "Choose one of the following...");
				selector.setAttribute("data-width", "500px");
				selector.setAttribute("onchange", "nextButton2(results)");
				document.getElementById("temp").appendChild(selector);
				listItem = document.createElement("option");
				listItem.setAttribute("value", "1");
				listItem.innerHTML = "Use both Campaigns and Keywords to create Placements Groups";
				document.getElementById("question3_" + i).appendChild(listItem);
				listItem2 = document.createElement("option");
				listItem2.setAttribute("value", "2");
				listItem2.innerHTML = "Use only Campaigns to create Placements Groups";
				document.getElementById("question3_" + i).appendChild(listItem2);
				listItem3 = document.createElement("option");
				listItem3.setAttribute("value", "3");
				listItem3.innerHTML = "Use only Keywords to create Placements Groups";
				document.getElementById("question3_" + i).appendChild(listItem3);

				$("#question3").selectpicker('refresh');

				$('.selectpicker').selectpicker({});
			}
			if (!opt.selected && exist != null) {
				var index = results.indexOf(i);
				results.splice(index, 1);
				ckw.splice(index, 1);
				var index2 = placements.indexOf(opt);
				placements.splice(index2, 1);
				var selector = document.getElementById("question3_" + i);
				selector.parentNode.parentNode.removeChild(selector.parentNode);
				var question = document.getElementById(i);
				document.getElementById("temp").removeChild(question);
			}
		}
		nextButton2(results);
	});
});

function cleanGlobals() {
	$.ajax({
		url : "./DcmApi/CleanGlobals",
		type : "GET"
	});
}