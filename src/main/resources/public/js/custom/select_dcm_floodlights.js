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

function listAllFloodlights() {
	$.ajax({
		url : "./DcmApi/GetAllDcmFloodlights",
		type : "POST",
		success : function(data) {
			for (key in data) {
				addFloodlightsToList(key, data[key]);
			}
			tableIt();
		}
	});
}

function checkAll() {
	if (document.getElementById("selectall").checked) {
		document.getElementById('submitBtn').disabled = false;
	} else {
		document.getElementById('submitBtn').disabled = true;
	}
}

function addFloodlightsToList(id, name) {

	TR = document.createElement("TR");
	TD = document.createElement("TD");
	var t = document.createTextNode(name);
	TD.appendChild(t);

	TR.appendChild(TD);

	TD = document.createElement("TD");
	input = document.createElement("input");
	input.type = "checkbox";
	input.name = "floodlight";
	input.className = "floodlightCheckbox";
	input.id = id;
	input.onclick = function() {
		var floodlights = document.getElementsByName("floodlight");
		for (i = 0; i < floodlights.length; i++) {
			if (floodlights[i].checked == true) {
				document.getElementById('submitBtn').disabled = false;
				break;
			} else {
				document.getElementById('submitBtn').disabled = true;
			}
		}
	};
	TD.appendChild(input);

	TR.appendChild(TD);

	document.getElementById("data").appendChild(TR);

}

function tableIt() {
	if ($.fn.dataTable.isDataTable('#table')) {
		table = $('#table').dataTable();
	} else {

		table = $('#table').dataTable({ // Apparently the uppercase or lowercase
			// 'd' in datatable makes a difference
			"scrollCollapse" : true,
			"paging" : false,
			"searching" : false,
			"info" : false,
			"ordering" : true
		});

		table.fnAdjustColumnSizing();
	}
}

function checkbox() {
	if (document.getElementById('yes').checked || document.getElementById('no').checked) {
		document.getElementById('submitBtn').disabled = false;
	} else {
		document.getElementById('submitBtn').disabled = true;
	}
}

function submitFloodlights() {
	// collect selected pages
	var jsonData = [];

	var floodlights = document.getElementsByName("floodlight");
	for (i = 0; i < floodlights.length; i++) {
		if (floodlights[i].checked == true) {
			jsonData.push(floodlights[i].id)
		}
	}
	var loadingSpinner = document.createElement("div");
	loadingSpinner.className = "spinner";
	loadingSpinner.id = "spinner";
	loadingSpinner.style.display = "none";
	var loadingImage = document.createElement("img");
	loadingImage.id = "img-spinner";
	loadingImage.src = "/img/ajax-loader.gif";
	loadingImage.alt = "Loading";
	loadingSpinner.appendChild(loadingImage);

	document.getElementsByTagName('body')[0].appendChild(loadingSpinner);

	$(document).ajaxSend(function() {
		$("#spinner").show();
	});
	$(document).ajaxStop(function() {
		$("#spinner").hide();
	});
	$(document).ajaxError(function() {
		$("#spinner").hide();
	});

	$.ajax({
		url : "./DcmApi/SetFloodLights",
		type : "POST",
		data : JSON.stringify(jsonData),
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		success : function() {
			$.ajax({
				url : "./DcmApi/RunDcmFile",
				type : "POST",
				data : JSON.stringify(jsonData),
				dataType : "json",
				contentType : "application/json; charset=utf-8",
				success : function() {
					$.ajax({
						url : "./FileUpload/File",
						type : "POST",
						success : function() {
							window.location.href = "beforeGrouping.html";
						}
					})
				}
			});
		}
	});
}
