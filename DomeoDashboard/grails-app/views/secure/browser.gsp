<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
<head>
<g:javascript library="jquery" plugin="jquery"/>
<meta name="layout" content="domeo-secure" />
<title>Secured Area - Domeo Browser</title>
<style>
	ul.bar
	{
	list-style-type:none;
	margin:0;
	padding:0;
	overflow:hidden;
	}
	ul.bar li
	{
	float:left;
	}
	
	ul.bar a
	{
	display:block;
	padding-left:10px;
	padding-right: 10px;
	color: white;
	font-weight: bold;
	text-decoration: none;
	text-transform:uppercase;
	}
	
	#btn_s{
	    width:100px;
	    margin-left:auto;
	    margin-right:auto;
	}
	
	#btn_i {
	    width:125px;
	    margin-left:auto;
	    margin-right:auto;
	}

</style>
<script type="text/javascript">

	function edit(annotationId, url) {
		document.location = '${appBaseUrl}/web/domeo?url=' + encodeURIComponent(url) + '&setId=' + encodeURIComponent(annotationId);
	}

	function display(userId) {
		document.location = '${appBaseUrl}/secure/user/' + userId;
	}

	function displayUser(userId) {
		document.location = '${appBaseUrl}/secure/user/' + userId;
	}

	function displaySet(annotationUri) {
		document.location = '${appBaseUrl}/secure/set/' + encodeURIComponent(annotationUri);
	}

	function displayHistory(annotationSetUri) {
		document.location = '${appBaseUrl}/secure/setHistory/' + encodeURIComponent(annotationSetUri);
	}

	function displayShare(annotationId) {
		open_in_new_tab('${appBaseUrl}/share/set/' + encodeURIComponent(annotationId));
	}

	function displayByUrlShare(url) {
		open_in_new_tab('${appBaseUrl}/share/sets/?url=' + encodeURIComponent(url));
	}
	
	function open_in_new_tab(url)
	{
	  var win=window.open(url, '_blank');
	  win.focus();
	}

		

	function displayAccessType(accessType) {
		if(accessType=='urn:domeo:access:public') {
			return ", <img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'world16x16.png',plugin:'users-module')}\" /> Public"
		} else if(accessType=='urn:domeo:access:private') {
			return ", <img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'personal16x16.png',plugin:'users-module')}\" /> Private"
		} else if(accessType=='urn:domeo:access:groups') {
			return ", <img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'group16x16.png',plugin:'users-module')}\" /> Restricted"
		}
	}

	function displayLock(lock) {
		if(lock=='true') {
			return ", <img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'lock16x16.png',plugin:'users-module')}\" /> Locked"
		} else {
			return "";
		}
	}

	function isLocked(lock) {
		return lock=='true';
	}

	function getModifyLink(item) {
		return "<a onclick=\"javascript:edit('" + item.lastAnnotationSetIndex.lastVersion.individualUri + "', '" + item.lastAnnotationSetIndex.lastVersion.annotatesUrl + "')\" style=\"text-decoration: none; cursor: pointer;\"><img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'black_edit.gif',plugin:'users-module')}\" /> Document</a><br/>";
	}

	function getExploreLink(item) {
		return "<a onclick=\"javascript:displaySet('" + item.lastAnnotationSetIndex.lastVersion.individualUri + "')\" style=\"text-decoration: none; cursor: pointer;\"><img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'clipboard-list.png',plugin:'users-module')}\" /> Browse</a><br/>";
	}

	function getShareLink(item) {
		return "<a onclick=\"javascript:displayShare('" + item.lastAnnotationSetIndex.lastVersion.individualUri + "')\" style=\"text-decoration: none; cursor: pointer;\"><img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'block-share.png',plugin:'users-module')}\" /> Share</a><br/>";
	}

	function getShareByUrlLink(url) {
		return "<a onclick=\"javascript:displayByUrlShare('" + url + "')\" style=\"text-decoration: none; cursor: pointer;\"><img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'block-share.png',plugin:'users-module')}\" /> Export listed sets</a><br/>";
	}

	function getHistoryLink(item) {
		if(item.lastAnnotationSetIndex.lastVersion.versionNumber>1) 
		return "<a onclick=\"javascript:displayHistory('" + item.lastAnnotationSetIndex.lastVersion.individualUri + "')\" style=\"text-decoration: none; cursor: pointer;\"><img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'history.png',plugin:'users-module')}\" /> History</a><br/>";
		else return "";
	}

	function getTarget(item) {
		var u = item.lastAnnotationSetIndex.lastVersion.annotatesUrl;
		var temp = String(u);
		if(temp.length>60) {
			u = temp.substring(0, 30) + '...' + temp.substring(temp.length-25);
		}
		return "On  <a href='#' onclick='javascript:loadData(\""+item.lastAnnotationSetIndex.lastVersion.annotatesUrl+"\")'>"+ u + " <img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'show.gif',plugin:'users-module')}\" /></a> ";
	}

	function getTargetOut(item) {
		var u = item;
		var temp = String(u);
		if(temp.length>60) {
			u = temp.substring(0, 30) + '...' + temp.substring(temp.length-25);
		}
		return "Annotation sets for:<br/>  <a target='_blank' href='"+item+"'>"+ u + " <img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'external.png',plugin:'users-module')}\" /></a> ";
	}

	function getProvenance(item) {
		return 'By <a onclick=\"javascript:displayUser(\'' + item.lastAnnotationSetIndex.lastVersion.createdBy.id + '\')\" style=\"cursor: pointer;\">' + item.lastAnnotationSetIndex.lastVersion.createdBy.displayName + '</a> (et al) on ' + item.lastAnnotationSetIndex.lastVersion.createdOn + ' with v. ' + item.lastAnnotationSetIndex.lastVersion.versionNumber;
	}

	function getStats(item) {
		return '# annotation items: '+item.lastAnnotationSetIndex.lastVersion.size + displayAccessType(item.permissionType) + displayLock(item.isLocked);
	}

	function retrieveCitation(item) {
		var dataToSend = { url: item.lastAnnotationSetIndex.lastVersion.annotatesUrl, annotationId: item.lastAnnotationSetIndex.lastVersion.id };
		$.ajax({
			url: "${appBaseUrl}/ajaxBibliographic/url",
	  	  	context: $("#resultsList"),
	  	  	data: dataToSend,
	  	  	success: function(data){
		  	  	if(data.message) {	
		  	  		$("#citation-"+item.lastAnnotationSetIndex.lastVersion.id).html(
				  	  		"<img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'black_info.gif',plugin:'users-module')}\" /> " +
				  	  		data.message  
		  	  			);
		  	  	}
	  	  	}
		});
	}

	function loadData(url) {
		$("#resultsList").empty();
		var dataToSend = { id: '${loggedUser.id}', documentUrl: url };
		$.ajax({
	  	  	url: "${appBaseUrl}/ajaxPersistence/annotationSets",
	  	  	context: $("#resultsList"),
	  	  	data: dataToSend,
	  	  	success: function(data){
	  			$("#progressIcon").css("display","none");
	  			if(!data.annotationListItemWrappers || data.annotationListItemWrappers==null
	  		  			|| data.annotationListItemWrappers==undefined) {
	  				$("#resultsSummary").html("");
					$("#resultsList").html("No results to display");
		  		} else {
		  			var label = data.annotationListItemWrappers.length == 1 ? data.annotationListItemWrappers.length + ' Set' : data.annotationListItemWrappers.length + ' Sets';
		  			$("#resultsSummary").html("Displaying <span style='font-weight: bold;''>"+label+"</span> out of " + data.totalResponses);
		  			if(data.latestContributor) {
			  			$("#resultsStats").html("Last by " + "<a onclick=\"javascript:display('" + data.latestContributor.id + "')\" style=\"cursor: pointer;\">" + 
			  		  			data.latestContributor.displayName + "</a><br/> on " + data.latestContribution);
			  		}
			  		if(url) {
				  		$('#resultsList').append(getTargetOut(url)); 
				  		$('#resultsList').append("<br/>");
				  		$('#resultsList').append(getShareByUrlLink(url));
				  		$('#resultsList').append("<br/>");
				  		window.history.pushState({url:url},"", window.location+'/?url='+url);
			  		}

			  		var users = new Array();
		  			$.each(data.annotationListItemWrappers, function(i,item){
		  				$('#resultsList').append('<div style="border: 1px solid #eee; padding: 3px;"><table width="100%"><tr><td>' +
		  					'<span style="font-weight: bold;">'+item.lastAnnotationSetIndex.lastVersion.label + '</span>: ' + item.lastAnnotationSetIndex.lastVersion.description +
		  					'<br/>' +
		  					getProvenance(item) +
		  					'<br/>' +
		  					getStats(item) + 
		  					'<br/>' +
		  					getTarget(item)  +
		  					'<div id="citation-'+item.lastAnnotationSetIndex.lastVersion.id+'"><img id=\"groupsSpinner\" src=\"${resource(dir:'images',file:'spinner.gif',plugin:'users-module')}\" /> Retrieving Citation</div>' +
		  					'</div>' +
		  					'</td>' +
		  					'<td width="90px">' +
		  					getModifyLink(item) +
		  					getExploreLink(item) +
		  					getShareLink(item) +
		  					getHistoryLink(item) + 
				  			'</td>' + 
		  					'</tr></table>' 
		  					
		  					//getTarget(item)  +
		  					//'<div id="citation-'+item.lastAnnotationSetIndex.lastVersion.id+'"><img id=\"groupsSpinner\" src=\"${resource(dir:'images',file:'spinner.gif',plugin:'users-module')}\" /> Retrieving Citation</div>' +
		  					//'</div>' +
		  					
		  					//'<br/>'
							
		  					);
		  				retrieveCitation(item);
						users[i] = item.lastAnnotationSetIndex.lastVersion.createdBy;
		  				//alert(item.lastAnnotationSetIndex.lastVersion.createdBy.displayName);
		  				//$('#resultsList').append('<input type="checkbox" name="vehicle" value="Bike">' 
		  		  		//		+ item.group.name + '<br/>'); 
		  		  				 
				  				//item.dateCreated + '</td><td>'+ roles +
				  				//'</td><td> '+ item.status.label + '</td></tr>');
		  		    });	
		  		}  			
		  	}
	  	});
	}

	$(document).ready(function() {
		$('#progressIcon').css("display","block");
		loadData();
	});
</script>
</head>
<body>
  <div class="content">
    <div class="content_resize">
	    <div class="sidebar" style="padding-top: 30px;padding-bottom: 30px; padding-right:2px;">
	    	<div align="center" style="background: #cc3300; padding: 5px; color: #fff; font-weight: bold;">Filter (not implemented yet)</div>
	    	<div style="background: #fff; padding: 5px; padding-top: 10px; border: 2px solid #cc3300;">
	    		<%--
	    		<div align="left" style="padding-left:4px; background: #FFCC00"><b>By Text</b><br/></div>
			    <input type="text" name="search" style="width: 220px;"><br><br>
			    --%>
			    
			    <%--
			    <div align="left" style="padding-left:4px; background: #FFCC00"><b>By Editor</b><br/></div>
			    <input type="checkbox" name="vehicle" value="Bike" checked>All <input type="checkbox" name="vehicle" value="Bike" checked>Mine<br>
			    <br>
			    --%>

			    <div align="left" style="padding-left:4px; background: #FFCC00"><b>By Access</b><br/></div>
			    <input type="checkbox" name="vehicle" value="Public" checked="checked">Public<br>
			    <input type="checkbox" name="vehicle" value="Groups">Groups<br>
			    
			  	<div id="groupsList">
			  		<g:each in="${userGroups}" status="i" var="usergroup">
			  			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="checkbox" name="${usergroup.group.name}" value="Car">${usergroup.group.name}<br/>
			  		</g:each>
			  	</div>
			    
				<input type="checkbox" name="vehicle" value="Private">Private<br/>
			  	
			  		
				<br/>
				<div align="center"><input value="Refresh" title="Search" name="lucky" type="submit" id="btn_i"></div>
			</div>
			<br/>
			<div align="center" style="background: #cc3300; padding: 5px; color: #fff; font-weight: bold;">People</div>
	    	<div style="background: #fff; padding: 5px; padding-top: 10px; border: 2px solid #cc3300;">
	    	
	    	</div>
	  	</div>
 
 		<!-- Browsing Navigation -->
	    <div style="background: #cc3300; color: #fff;">
	   		<ul class="bar">
				<li><g:link controller="secure" action="browser"><span>Annotation Sets</span></g:link></li>
				<li><g:link controller="secure" action="documents"><span>Documents</span></g:link></li>
				<li><a href="#">Bibliography</a></li>
			</ul> 
	    </div>
	    
	    <div id="progressIcon" align="center" style="padding: 5px; padding-left: 10px; display: none;"><img id="groupsSpinner" src="${resource(dir:'images',file:'progress-bar-2.gif',plugin:'users-module')}" /></div>
	    <table width="730px;">
	    	<tr><td>
	    		<div id="resultsSummary" style="padding: 5px; padding-left: 10px;"></div>
	    	</td><td style="text-align:right">
	    		<div id="resultsStats" style="padding: 5px; "></div>
	    	</td></tr>
	    </table>
	    <div id="resultsList" style="padding: 5px; padding-left: 10px; width: 715px;"></div>
	    <div class="resultsPagination"></div>
      	<div class="clr"></div>
    </div>
  </div>
</body>
</html>
