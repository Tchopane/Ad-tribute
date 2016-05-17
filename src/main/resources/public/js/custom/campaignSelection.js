function getReport(fileID, reportID) {
	console.log("test")
	$.ajax({
		url: "./DcmApi/GetReport",
		type: "GET",
		success: function(data)
		{
		}
	});
}