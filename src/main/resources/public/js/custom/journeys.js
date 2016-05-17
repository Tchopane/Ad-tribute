function selectClean(){
	if (document.getElementById("exclude1").checked) {
		var exclude = true;
	} else {
		var exclude = false;
	}
	if (document.getElementById("action1").checked) {
		var action = 1;
	} else if (document.getElementById("action2").checked) {
		var action = 2;
	} else {
		var action = 3;
	}
	$.ajax ({
		url: "./CleanActivities/SetExcludeAndAction?exclude="+exclude+"&action="+action,
		type: "GET",
	    success: function(){
	    	$.ajax({
				url : "./CleanActivities/Fill",
				type : "POST",
				success : function(){
					window.location.href = "PrepareInteractions.html";
				}
			})
		}
	});
}

function prepareInteractions() {
	$.ajax ({
		url: "./PlacementGroup/PreparePlacements",
		type: "POST",
	    success: function(){
	    	window.location.href = "WeightInteractions.html";
		}
	});
}

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