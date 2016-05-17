$(document).ready(function() {
	$('.selectpicker').selectpicker('refresh');
});

//scroll the message box to the top offset of browser's scroll bar
$(window).scroll(function() {
	$('#message_box').animate({
		top : $(window).scrollTop() + 50 + "px"
	}, {
		queue : false,
		duration : 100
	});
});

var numberOfKW;
var maxExistingGroup;
var h = "38px";
var w = "90px";
var speed = 300;
var title = "";

function cleanGlobals() {
	$.ajax({
		url : "./DcmApi/CleanGlobals",
		type : "GET"
	});
}

function numberMaxGroup(callback) {
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
				url : "./DcmApi/GetGroupingPlacements",
				type : "GET",
				success : function(data) {

					for (key in data) {
						var p = document.createElement("p");
						var br = document.createElement("br");

						var newDiv = document.createElement("div");
						newDiv.id = "campaign_" + key;
						newDiv.className = "campaign-div";
						newDiv.style.display = "inline-block";
						newDiv.style.cursor = "pointer";
						newDiv.setAttribute("name", "campaign");
						newDiv.innerHTML = data[key];
						newDiv.style.backgroundColor = '#FFDC00';
						newDiv.value = 0;
						document.getElementById("campaignsList").appendChild(newDiv);
						document.getElementById("campaignsList").appendChild(p);

						$("#campaign_" + key).draggable({
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
	//Count the amount of existing buttons
	var nbButtons = document.getElementsByName("group").length;

	//Determines the biggest number on the existing buttons
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
	newButton.setAttribute("contenteditable", "false");
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

	//Make it droppable
	$("#group" + displayedNum).droppable({
		activeClass : "ui-state-default",
		tolerance : "pointer",
		drop : function(event, ui) {
			$(this).addClass("ui-state-highlight");
			var movedDiv = document.getElementById(ui.draggable.context.id);
			if (movedDiv.id.split("_")[0].localeCompare("campaign") === 0) {
				movedDiv.value += 1;
				if (movedDiv.value == 1) {
					movedDiv.style.backgroundColor = '#9FFF33';
				} else {
					movedDiv.style.backgroundColor = '#1f7a1f';
				}
				var dataToSend = [];
//change has been made here				
				dataToSend.push(document.getElementById("group" + displayedNum).children[0].value);

				var campaign = ui.draggable.context.textContent;
				$.ajax({
					url : "./PlacementGroup/CheckIfPossibleCampaign?campaign=" + campaign,
					type : "GET",
					success : function(data) {
						if (data == 1) {
							dataToSend.push(ui.draggable.context.textContent);
							dataToSend.push('DONOTCONSIDER');
							//let's create a hidden div in the group div 
							//this hidden div will be displayed when hovering with the mouse
							var hiddenDiv = document.createElement("div");
							hiddenDiv.className = "hidden-div";
							if (movedDiv.value >= 1) {
								hiddenDiv.id = "hiddencampaign_" + movedDiv.id.split("_")[1] + "_" + movedDiv.value;
							} else {
								hiddenDiv.id = "hiddencampaign_" + movedDiv.id.split("_")[1];
							}
							hiddenDiv.style.display = 'none';
							hiddenDiv.innerHTML = movedDiv.innerHTML;
							hiddenDiv.style.cursor = "pointer";
							document.getElementById("group" + displayedNum).appendChild(hiddenDiv);
							$("#" + hiddenDiv.id).draggable({
								revert : true,
								cursor : "pointer"
							});

						} else {
							dataToSend = [];
						}
						$.ajax({
							url : "./PlacementGroup/SetGroups",
							type : "POST",
							data : JSON.stringify(dataToSend),
							dataType : "json",
							contentType : "application/json; charset=utf-8",
							success : function(data) {
								if (data === 0) {
									var toDel = document.getElementById("message_box");
									if (toDel != undefined) {
										document.getElementsByTagName('body')[0].removeChild(toDel);
									}

									var newDiv = document.createElement("div");
									newDiv.id = "message_box";
									newDiv.innerHTML = "Sorry, but this campaign does not have enough corresponding keywords to be assigned to so many groups.";

									var pic = document.createElement("img");
									pic.id = "close_message";
									pic.style.float = "right";
									pic.style.cursor = "pointer";
									pic.style.zIndex = "10000";
									pic.src = "images/12-em-cross.png";
									newDiv.appendChild(pic);

									document.getElementsByTagName('body')[0].appendChild(newDiv);

									//when the close button at right corner of the message box is clicked
									$('#close_message').click(function() {
										//the messagebox gets scrool down with top property and gets hidden with zero opacity
										$('#message_box').animate({
											top : "+=15px",
											opacity : 0
										}, "slow");
									});

									if (movedDiv.value == 2) {
										movedDiv.value -= 1;
										movedDiv.style.backgroundColor = '#9FFF33';
									}
								} else if (data === 2) {
									var newDiv = document.createElement("div");
									newDiv.id = "message_box";
									newDiv.innerHTML = "This campaign has already been assigned to that group.";

									var pic = document.createElement("img");
									pic.id = "close_message";
									pic.style.float = "right";
									pic.style.cursor = "pointer";
									pic.src = "images/12-em-cross.png";
									newDiv.appendChild(pic);

									document.getElementsByTagName('body')[0].appendChild(newDiv);

									//when the close button at right corner of the message box is clicked
									$('#close_message').click(function() {
										//the messagebox gets scrool down with top property and gets hidden with zero opacity
										$('#message_box').animate({
											top : "+=15px",
											opacity : 0
										}, "slow");
									});

									if (movedDiv.value == 2) {
										movedDiv.value -= 1;
										movedDiv.style.backgroundColor = '#9FFF33';
									}
								}
							},
							complete : function() {
								var all = true;
								var countGreen = 0;
								var list = document.getElementsByName("campaign");
								for (c in list) {
									if (list[c].value === 0) {
										all = false;
										break;
									}
									if (list[c].value >= 2) {
										countGreen += 1;
									}
								}
								if (all && (countGreen != 0)) {
									document.getElementById("kwBtn").disabled = false;
								}
							}
						});
					}
				});
				$("#group" + displayedNum).removeClass("ui-state-highlight");
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

					//No need to use the CheckIfPossible method here because we are just moving the campaign from a group to another one so it is possible
					var dataToSend = [];
//change made here					
					dataToSend.push(oldGroup.children[0].value);
//change made here					
					dataToSend.push(document.getElementById("group" + displayedNum).children[0].value);
					dataToSend.push(ui.draggable.context.textContent);
					dataToSend.push('DONOTCONSIDER');

					//And we can update the globals : we use another method since the dataToSend is not in the same format
					$.ajax({
						url : "./PlacementGroup/SetGroups2",
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
		for (var i = 1; i < c.length; i++) { //Start at 1 because we want the name of the group to still be displayed
			c[i].style.display = "none";
		}
		$(this).stop().animate({
			height : h,
			width : w
		}, speed);

	});

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

}

function removeGroup() {
	var options = document.getElementById("removeGroup").options;
	for (var j = 0, iLen2 = options.length; j < iLen2; j++) {
		var opt = options[j];
		if (opt.selected) {

			var stringtemp = opt.id.split("_");
			var newId = stringtemp[1];
			var button = document.getElementById(newId);
			if (button.childElementCount != 0) {
				//First, ajax Request to update the globals and remove the concerned Group
				var groupToRemove = [];
				groupToRemove.push(button.children[0].value);
				$.ajax({
					url : "./PlacementGroup/RemoveGroup",
					type : "POST",
					data : JSON.stringify(groupToRemove),
					dataType : "json",
					contentType : "application/json; charset=utf-8",
					success : function(data) {
						//Then, update the colors of the campaigns and downgrade their value field
						var campaignslist = document.getElementsByName("campaign");
						for (var i = 0; i < data.length; i++) {
							for (var j = 0; j < campaignslist.length; j++) {
								if (data[i].localeCompare(campaignslist[j].textContent) === 0) {
									campaignslist[j].value -= 1;
									if (campaignslist[j].value == 1) {
										campaignslist[j].style.backgroundColor = '#9FFF33';
									} else {
										campaignslist[j].style.backgroundColor = '#FFDC00';
									}
								}
							}
						}
					}
				});

			}
			document.getElementById("newGroupDiv").removeChild(button);
			listItem = document.getElementById(opt.id);
			document.getElementById("removeGroup").removeChild(listItem);
			break;
		}
	}
	$("#removeGroup").selectpicker('refresh');
}

function makeVisible() {
	//We disable the add group button and the set keywords buttons
	var button = document.getElementById("newGroup");
	button.disabled = true;

	var button2 = document.getElementById("kwBtn");
	button2.disabled = true;

	//We also need to disable drag&drop for the campaigns
	var campaigns = document.getElementsByClassName("campaign-div");
	for (var i = 0, iLen = campaigns.length; i < iLen; i++) {
		var tempId = campaigns[i].id;
		$("#" + tempId).draggable("option", "disabled", true);
	}

	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (xhttp.readyState == 4 && xhttp.status == 200) {
			var json = JSON.parse(xhttp.responseText); // result of the java method
			var jsonLen = json.length;
			var index = 1; //index est le numero de la campagne en cours
			while (json.length > 0) {

				var cLen = 1; // cLen est le nombre de lignes dans json tel que la campagne est toujours la même
				for (i = 0; i < json.length; i++) {
					if (i == json.length - 1) {
						break;
					}
					if (json[i + 1].split("/")[0].localeCompare(json[i].split("/")[0]) === 0) {
						cLen += 1;
					} else {
						break;
					}
				}
				var currentCampaign = json[0].split("/")[0];
				var title = document.createElement("h4");
				title.innerHTML = currentCampaign;
				document.getElementById("keywordsList").appendChild(title);

				var kLen = 1; // kLen est le nombre de keywords differents pour cette campagne
				for (i = 0; i < cLen - 1; i++) {
					if (json[i + 1].split("/")[1].localeCompare(json[i].split("/")[1]) === 0) {
						kLen += 1;
					} else {
						break;
					}
				}

				var gLen = cLen / kLen; // gLen le nombre de groupes differents pour cette campagne

				var rowDiv = document.createElement("div");
				rowDiv.id = "campaign" + index;
				document.getElementById("keywordsList").appendChild(rowDiv);

				for (j = 0; j < gLen; j++) {
					var selector = document.createElement("select");
					selector.setAttribute("id", "campaign_" + index + "_selector_" + j);
					selector.setAttribute("multiple", "multiple");
					selector.setAttribute("class", "form-control selectpicker toCheck");
					selector.setAttribute("data-live-search", "true");
					selector.setAttribute("name", "selector_" + index);
					selector.setAttribute("onchange", "updateSelect(this)");
					var stringTitle = json[j * kLen + 1].split("/")[1];
					selector.setAttribute("title", stringTitle);
					selector.setAttribute("data-header", stringTitle.concat(" || ").concat(currentCampaign));

					selector.setAttribute("data-size", "5");
					document.getElementById("campaign" + index).appendChild(selector);
					for (k = 0; k < kLen; k++) {
						var listItem = document.createElement("option");
						listItem.setAttribute("value", k + 1);
						listItem.id = "campaign_" + index + "_selector_" + j + "_option_" + k;
						listItem.innerHTML = json[k].split("/")[2];
						document.getElementById("campaign_" + index + "_selector_" + j).appendChild(listItem);
					}

					$("#campaign_" + index + "_selector_" + j).selectpicker('refresh');
				}

				index += 1;
				json.splice(0, cLen);
				var space = document.createElement("br");
				var space2 = document.createElement("p");

				document.getElementById("keywordsList").appendChild(space);
				document.getElementById("keywordsList").appendChild(space2);
			}
			var lastspace = document.createElement("div");
			lastspace.id = "space";
			document.getElementById("second").appendChild(lastspace);
		}
	}
	xhttp.open("GET", "/PlacementGroup/GetMultipleGroupsAndKeywords", true);
	xhttp.send();

	document.getElementById("second").style.display = 'block';
	var lastElementTop = $('#keywords').position().top;
	$("html, body").animate({
		scrollTop : $("#second").offset().top
	}, 1000);
}

function updateSelect(sel) {
	//on commence en recuperant la liste de tous les selectionnés dans le selector no j
	var listSelected = [];
	var options = sel.options;
	for (var p = 0, iLen2 = options.length; p < iLen2; p++) {
		var opt = options[p];
		if (opt.selected) {
			listSelected.push(opt);
		}
	}

	//on parcourt tous les autres selector de la même campagne
	var ID = sel.id;
	var stringtemp = ID.split("_");
	var index = stringtemp[1];
	var j = parseInt(stringtemp[3]);
	//on veut aussi recuperer gLen, le nombre de groupes pour cette campagne
	var gLen = document.getElementsByName(sel.name).length;

	for (i = 0; i < gLen; i++) {
		if (i === j) {
			continue;
		}
		var selector = document.getElementById("campaign_" + index + "_selector_" + i);

		var options = document.getElementById("campaign_" + index + "_selector_" + i).options;
		if (listSelected.length != 0) {
			loopk: for (var k = 0, iLen2 = options.length; k < iLen2; k++) {
				for (var n = 0, listLen = listSelected.length; n < listLen; n++) {

					var opt = options[k];
					if (listSelected[n].textContent.localeCompare(opt.textContent) === 0) {
						listItem = document.getElementById(opt.id);
						listItem.setAttribute("disabled", "disabled");
						continue loopk;
					} else {
						opt.removeAttribute("disabled");
					}
				}

			}
		} else {
			for (var k = 0, iLen2 = options.length; k < iLen2; k++) {
				var opt = options[k];
				opt.removeAttribute("disabled");
			}
		}
		$("#campaign_" + index + "_selector_" + i).selectpicker('refresh');
	}

	//Let's check if we can unlock the last button
	var selectors = document.getElementsByClassName("toCheck");
	var amountNotSel = 0;
	dance: for (var x = 0; x < selectors.length; x++) {
		var options = selectors[x].options;
		for (var i = 0, iLen = selectors[x].length; i < iLen; i++) {
			opt = options[i];
			if (!opt.selected && !opt.disabled) {
				amountNotSel += 1;
				break dance;
			}
		}
	}
	if (amountNotSel === 0) {
		var button = document.getElementById("kwBtn2");
		button.disabled = false;
		document.getElementById("next").style.display = 'none';
		document.getElementById("next2").style.display = 'block';
	} else {
		var button = document.getElementById("kwBtn2");
		button.disabled = true;
	}
}

function goNext() {
	var selectors = document.getElementsByClassName("form-control selectpicker toCheck");
	var matrixToSend = [];
	for (var x = 0; x < selectors.length; x++) {
		var dataToSend = [];
		var group = selectors[x].title;
		console.log(group);
		dataToSend.push(group);
		dataToSend.push('DONOTCONSIDER');
		var keywords = [];
		for (var j = 0; j < selectors[x].options.length; j++) {
			if (selectors[x].options[j].selected) {
				keywords.push(selectors[x].options[j].textContent.concat("/_").concat(selectors[x].getAttribute("data-header").split(" || ")[1]));
				console.log(keywords.push(selectors[x].options[j].textContent.concat("/_").concat(selectors[x].getAttribute("data-header").split(" || ")[1])));
			} //instead of just adding the keywords, we want to add in the dataToSend list something like : "keyword/_campaign"
		}
		var temp = dataToSend.concat(keywords);
		dataToSend = temp;
		matrixToSend.push(dataToSend);
	}
	var loadingSpinner = document.createElement("div");
	loadingSpinner.className = "spinner";
	loadingSpinner.id = "spinner";
	loadingSpinner.style.display = "none";
	loadingSpinner.style.zIndex = "1005";
	var loadingImage = document.createElement("img");
	loadingImage.id = "img-spinner";
	loadingImage.src = "/img/ajax-loader.gif";
	loadingImage.alt = "Loading";
	loadingSpinner.appendChild(loadingImage);

	document.getElementsByTagName('body')[0].appendChild(loadingSpinner);

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

	$.ajax({
		url : "./PlacementGroup/SetGroupsMatrix",
		type : "POST",
		data : JSON.stringify(matrixToSend),
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		success : function() {
			$.ajax({
				url : "/PlacementGroup/SavePlacementGroups",
				type : "GET",
				success : function() {
					console.log('checkpoint 9');
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
	});
}