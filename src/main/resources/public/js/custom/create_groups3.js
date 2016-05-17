$(document).ready(function() {
	$('.selectpicker').selectpicker('refresh');
});

var maxExistingGroup;
var h = "38px";
var w = "90px";
var speed = 300;

function cleanGlobals() {
	$.ajax({
		url : "./DcmApi/CleanGlobals",
		type : "GET"
	});
}

function numberMaxGroup(callback){
	//Determines the maximal number of the already existing groups
	$.ajax({
		url : "./PlacementGroup/GetMaxGroup",
		type : "GET",
		success : callback
	});
}

function myCallback(result) {
    maxExistingGroup = result;
}

function pageContent() {
	$.ajax({
		url : "./DcmApi/GetPageTitle",
		type : "GET",
		success : function(data) {
			var title = document.createElement("h2");
			title.innerHTML = data;
			document.getElementById("title").appendChild(title);
			$.ajax({
				url : "/DcmApi/GetGroupingKeywords",
				type : "GET",
				success : function(data) {
					for (key in data) {
						var p = document.createElement("p");
						var br = document.createElement("br");

						var newDiv = document.createElement("div");
						newDiv.id = "keyword_" + key;
						newDiv.className = "campaign-div";
						newDiv.style.display = "inline-block";
						newDiv.style.cursor = "pointer";
						newDiv.setAttribute("name", "keyword");
						newDiv.innerHTML = data[key];
						newDiv.style.backgroundColor = '#FFDC00';
						newDiv.value = 1;
						document.getElementById("keywordsList").appendChild(newDiv);
						document.getElementById("keywordsList").appendChild(p);
						// document.getElementById("campaignsList").appendChild(br);
						$("#keyword_" + key).draggable({
							revert : true,
							cursor : "pointer"
						});
					}
				}
			});
		}
	});
}

function addGroup() {
	// Count the amount of existing buttons
	var nbButtons = document.getElementsByName("group").length;

	// Determines the biggest number on the existing buttons
	var list = document.getElementsByName("group");
	var maxNum = 0;

	for (key in list) {
		if (parseInt(list[key].value) > maxNum) {
			var maxNum = parseInt(list[key].value);
		}
	}
	
	var numberNewBut = nbButtons + 1;
	if (nbButtons === 0) {
		if (maxExistingGroup != undefined) {
			var displayedNum = maxNum + 1 + maxExistingGroup;		
		} else {
			var displayedNum = maxNum + 1;
		}
	} else {
		var displayedNum = maxNum + 1;
	}

	var p = document.createElement("p");
	document.getElementById("newGroupDiv").appendChild(p);
	var newButton = document.createElement("div");

	newButton.id = "group" + displayedNum;
	newButton.className = "group-div";
	newButton.setAttribute("name", "group");
	newButton.setAttribute("contenteditable", "true");
//	newButton.innerHTML = "Group " + displayedNum;
	var newInput = document.createElement("input");
	newInput.id = "input" + displayedNum;
	newInput.className = "transparent-input";
	newInput.value = "Group " + displayedNum;
	newInput.onchange = function() {
		 //retrieve old value and new value
		 $(this).data("old", $(this).data("new") || "Group " + displayedNum);
	     $(this).data("new", $(this).val());
	     console.log($(this).data("old"));
	     console.log($(this).data("new"));
		//update the removelist
	   //remove all content from the selectpicker
	 	var myNode = document.getElementById("removeGroup");
	 	while (myNode.firstChild) {
	 		myNode.removeChild(myNode.firstChild);
	 	}
	 	//update the form control selectpicker by adding the groups added
	 	var list = document.getElementsByName("group");
	 	for (key in list) {
	 		listItem = document.createElement("option");
	 		listItem.id = "optionid_" + list[key].id;
	 		var stringId = list[key].id;
	 		if (stringId != undefined) {
	 //changes made here			
	 			listItem.innerHTML = document.getElementById(stringId).children[0].value;
	 			document.getElementById("removeGroup").appendChild(listItem);
	 		}
	 	}
	 	$("#removeGroup").selectpicker('refresh');
	 	
	 	var oldname = $(this).data("old");
	 	var newname = $(this).data("new");
		//update the globals variables
		$.ajax({
			url : "./PlacementGroup/UpdateGroupName?oldname="+oldname+"&newname="+newname,
			type : "GET",
		})
	}
	
	newButton.appendChild(newInput);
	
	newButton.style.height = "38px";
	newButton.style.width = "90px";
	newButton.style.backgroundColor = "#FFDC00";
	newButton.value = displayedNum;
	document.getElementById("newGroupDiv").appendChild(newButton);

	// Make it droppable
	$("#group" + displayedNum).droppable({
		activeClass : "ui-state-default",
		tolerance : "pointer",
		drop : function(event, ui) {

			var movedDiv = document.getElementById(ui.draggable.context.id);
			if (movedDiv.id.split("_")[0].localeCompare("keyword") === 0) {
			movedDiv.style.display = 'none';
			movedDiv.value = 0;
			var dataToSend = [];
			
			var hiddenDiv = document.createElement("div");
			hiddenDiv.className = "hidden-div";
			hiddenDiv.id = "hiddenkeyword_" + movedDiv.id.split("_")[1];
			hiddenDiv.style.display = 'none';
			hiddenDiv.innerHTML = movedDiv.innerHTML;
			hiddenDiv.style.cursor = "pointer";
			document.getElementById("group" + displayedNum).appendChild(hiddenDiv);
			$("#" + hiddenDiv.id).draggable({
				revert : true,
				cursor : "pointer"
			});
			dataToSend.push(document.getElementById("group" + displayedNum).children[0].value);
			dataToSend.push('DONOTCONSIDER');
			dataToSend.push(ui.draggable.context.textContent);

			$.ajax({
				url : "./PlacementGroup/SetGroups",
				type : "POST",
				data : JSON.stringify(dataToSend),
				dataType : "json",
				contentType : "application/json; charset=utf-8",
				complete : function() {
					var all2 = true;
					var list2 = document.getElementsByName("keyword");
					for (c in list2) {
						if (list2[c].value === 1) {
							all2 = false;
							break;
						}
					}
					if (all2) {
						document.getElementById("kwBtn").disabled = false;
					}
				}
			});
			} else {
				//We start by moving the div movedDiv from the older group to the new one
				var oldGroup = movedDiv.parentElement;
				var doIt = true;
				var listHiddenDiv = $("#group" + displayedNum).children();
				for (var k = 0; k < listHiddenDiv.length; k++) {
					if (listHiddenDiv[k].textContent.localeCompare(movedDiv.textContent) === 0) {
						doIt = false;
						break;
					}
				}
				if (doIt) {
					document.getElementById("group" + displayedNum).appendChild(movedDiv);

					var dataToSend = [];
					dataToSend.push(oldGroup.children[0].value); //oldgroup
					dataToSend.push(document.getElementById("group" + displayedNum).children[0].value); //new group
					dataToSend.push('DONOTCONSIDER'); //empty
					dataToSend.push(ui.draggable.context.textContent); //name of keyword
					
					//And we can update the globals : we use another method since the dataToSend is not in the same format
					$.ajax({
						url : "./PlacementGroup/SetGroups3",
						type : "POST",
						data : JSON.stringify(dataToSend),
						dataType : "json",
						contentType : "application/json; charset=utf-8"
					});
				}
				$("#group" + displayedNum).removeClass("ui-state-highlight");
			}
		}
	});
	
	//Expand the group and show content when hovering with the mouse
	$("#group" + displayedNum).hover(function() {
		var c = document.getElementById("group" + displayedNum).children;
		for (var i = 0; i < c.length; i++) {
			c[i].style.display = "block";
		}
		$(this).stop().animate({
			height : "280px",
			width : "280px"
		}, speed);
	}, function() {
		var c = document.getElementById("group" + displayedNum).children;
		for (var i = 1; i < c.length; i++) {
			c[i].style.display = "none";
		}
		$(this).stop().animate({
			height : h,
			width : w
		}, speed);

	});

	// remove all content from the selectpicker
	var myNode = document.getElementById("removeGroup");
	while (myNode.firstChild) {
		myNode.removeChild(myNode.firstChild);
	}
	// update the form control selectpicker by adding the groups added
	var list = document.getElementsByName("group");
	for (key in list) {
		listItem = document.createElement("option");
		listItem.id = "optionid_" + list[key].id;
		var stringId = list[key].id;
		if (stringId != undefined) {
			listItem.innerHTML = document.getElementById(stringId).children[0].value;
			document.getElementById("removeGroup").appendChild(listItem);
		}
	}
	$("#removeGroup").selectpicker('refresh');

}

function removeGroup() {
	var options = document.getElementById("removeGroup").options;
	for (var j = 0, iLen2 = options.length; j < iLen2; j++) {
		var opt = options[j];
		if (opt.selected) {
			var stringtemp = opt.id.split("_");
			var newId = stringtemp[1];
			var button = document.getElementById(newId);
			document.getElementById("newGroupDiv").removeChild(button);
			listItem = document.getElementById(opt.id);
			document.getElementById("removeGroup").removeChild(listItem);
			break;
		}
	}
	$("#removeGroup").selectpicker('refresh');
}

function goNext() {
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
		url : "/PlacementGroup/SavePlacementGroups",
		type : "GET",
		success : function() {
			$.ajax({
				url : "./DcmApi/GetGroupingParametersAndUpdateGlobals",
				success : function(data) {
					if (data == 1) {
						window.location.href = "creatingGroups1.html";
					} else if (data == 2) {
						window.location.href = "creatingGroups2.html";
					} else if (data == 3) {
						window.location.href = "creatingGroups3.html";
					} else {
						window.location.href = "placements.html";
					}
				}
			});
		}
	});

}