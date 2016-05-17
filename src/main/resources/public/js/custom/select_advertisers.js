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

function listAllAdvertisers() {
	$
			.ajax({
				url : "./DcmApi/ListAllAdvertisers",
				type : "GET",
				success : function(data) {
					listItem = document.createElement("option");
					listItem.value = -1;
					listItem.innerHTML = "Select an Advertiser";
					listItem.selected = true;
					document.getElementById("advertiserList").appendChild(
							listItem);

					for (key in data) {
						listItem = document.createElement("option");
						listItem.value = key;
						listItem.innerHTML = data[key];
						document.getElementById("advertiserList").appendChild(
								listItem);
					}
					$("#advertiserList").selectpicker('refresh');
				}
			});
}

function selectAdvertiser() {
	var startDate = document.getElementById("startDate").value;
	var endDate = document.getElementById("endDate").value;
	var advertiser = document.getElementById("advertiserList").value;
	$.ajax({
		url : "./DcmApi/SetAdvertiserAndStartEndDate?clientId=" + advertiser
				+ "&startDate=" + startDate + "&endDate=" + endDate,
		type : "POST",
		success : function() {
			window.location.href = "selectDcmFloodlights.html";
		}
	});
}
