function addHeaderFooter() {
	var navHeader = document.createElement("nav");
	navHeader.className = "navbar navbar-fixed-top navbar-custom";
	
		var divContainerFluid = document.createElement("div");
		divContainerFluid.className = "container-fluid";
		navHeader.appendChild(divContainerFluid);
		
			var divNavbarHeader = document.createElement("div");
			divNavbarHeader.className = "navbar-header";
			divContainerFluid.appendChild(divNavbarHeader);
			
			var aNavbarBrand = document.createElement("a");
			aNavbarBrand.className = "navbar-brand";
			aNavbarBrand.setAttribute('href',"index.html");
			aNavbarBrand.innerHTML = "Ad-tribute";
			divNavbarHeader.appendChild(aNavbarBrand);
				
	document.getElementsByTagName('body')[0].appendChild(navHeader);
	
	var navFooter = document.createElement("nav");
	navFooter.className = "navbar navbar-fixed-bottom navbar-custom";
	
		var divContainerFluid = document.createElement("div");
		divContainerFluid.className = "container-fluid";
		navFooter.appendChild(divContainerFluid);
		
			var divRow = document.createElement("div");
			divRow.className = "row";
			divContainerFluid.appendChild(divRow);
				
				aImgLogo = document.createElement("a");
				aImgLogo.setAttribute('href',"http://www.mediabrands.com");
				divRow.appendChild(aImgLogo);
			
					var imgLogo = document.createElement("img");
					imgLogo.src = "img/logo.png";
					imgLogo.className = "resize img-responsive";
					imgLogo.style.margin = "auto";
					aImgLogo.appendChild(imgLogo);
				
	document.getElementsByTagName('body')[0].appendChild(navFooter);
}