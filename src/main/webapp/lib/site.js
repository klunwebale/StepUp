window.baseURL = '/stepup/api/controller';

function Dance() {
	
this.bindEvents = function() {
		
		$('#txtDanceSearch').autocomplete({
			minLength: 2,
		    delay: 100,
			select: function( event, ui ) {
				//$('#txtDanceSearch').data('dancename', ui.item.name);
				$('#txtDanceSearch').data('danceid',ui.item.id);
				$('#txtDanceSearch').data('dancetype',ui.item.type);
				$('#txtDanceSearch').data('dancename',ui.item.name);
				$('#txtDanceSearch').val(ui.item.name);
				return false;
			},
			
		    source: function (request, response) {
		    	$.ajax( {
		    		url: window.baseURL + '/searchAll?term='+request.term,
		    		type:"GET",
		    		success: function(data) {
		    			response(data);
		    		}
		    	});
		    }
		}).data("ui-autocomplete")._renderItem = function( ul, item ) {
			return $( "<li></li>" )
			.data( "item.autocomplete", item )
			.append( "<a href='#'>" + item.label + "</a>" )
			.appendTo( ul );
		};
		
		$('#txtDanceSearch2').autocomplete({
			minLength: 2,
		    delay: 100,
			select: function( event, ui ) {
				//$('#txtDanceSearch2').data('dancename', ui.item.name);
				$('#txtDanceSearch2').data('danceid',ui.item.id);
				$('#txtDanceSearch2').data('dancetype',ui.item.type);
				$('#txtDanceSearch2').data('dancename',ui.item.name);
				$('#txtDanceSearch2').val(ui.item.name);
				return false;
			},
			
		    source: function (request, response) {
		    	$.ajax( {
		    		url: window.baseURL + '/searchAll?term='+request.term,
		    		type:"GET",
		    		success: function(data) {
		    			response(data);
		    		}
		    	});
		    }
		}).data("ui-autocomplete")._renderItem = function( ul, item ) {
			return $( "<li></li>" )
			.data( "item.autocomplete", item )
			.append( "<a href='#'>" + item.label + "</a>" )
			.appendTo( ul );
		};
	
		
		var flag;
		$('.dance_action').bind('click',function(event){
			var a = $(this).data('action');
			switch(a) {
				case 'Moves':
					window.pageObj.flag=0;
					window.pageObj.findAllMoves($('#txtDanceSearch').val());
					break;
				case 'ClosestDance':
					window.pageObj.flag=0;
					window.pageObj.findClosesDance($('#txtDanceSearch').val());
					break;
				case 'Centrality':	
					window.pageObj.flag=0;
					window.pageObj.findCentrality($('#txtDanceSearch').val(), $('#txtDanceSearch').data('dancetype'),  $('#txtDanceSearch2').val(), $('#txtDanceSearch2').data('dancetype'));
					break;
				case 'Classification' :
					window.pageObj.flag=1;
					window.pageObj.getCriteriaDetails(document.getElementById("CateSearch").value,document.getElementById("CateSearch1").value);
					break;
				case '' :
					alert('In Progress');
					break;
			};
		});
		
	};
	
	this.getCriteriaDetails=function(term1,term2){
		$.ajax( {
    		url: window.baseURL + '/findNodesUnderACriteria',
    		data : {'term1' : term1,'term2':term2 },
    		dataType : 'json',
    		type:"POST",
    		success: function(data) {
		$('#dance_data').hide(); 
    			window.pageObj.rendergetCriteriaDetails(data); 

     		}
    	});
	};
	
this.rendergetCriteriaDetails = function(data) {
		
		sys = arbor.ParticleSystem();
		sys.parameters({stiffness:900, repulsion:3000, gravity:true, dt:0.015});
		
    					$("#viewport").html('');
	    sys.renderer = Renderer($("#viewport"));
	    sys.addNode(data['criteriaName'], {'color':'#FFCC00', 'shape' : 'dot', 'label' : data['criteriaName'], 'type' : 'Dance'} );
	    
	    for(var i in data['dances']) {
	    	var children = data['dances'][i];
	
	    		sys.addNode('dance'+ i, {'color':'#FF6699', 'shape' : 'dot', 'label' : children['danceName'], 'type' : 'Dance'});
	    		sys.addEdge('dance'+i,data['criteriaName'], {length : 0.7, label : 'has'+data['input'], name : 'has'+data['input'], directed : false});


	    		/*To render moves	 
	    		var moves=children['moves'];
	    		for(var j in moves){
	    			
	    			var submoves=moves[j];
	    			for(var k in submoves){
	    			sys.addNode('move'+ k, {'color':'blue', 'shape' : 'rect', 'label' : submoves[k], 'type' : 'Move'});
	    			sys.addEdge('dance'+ i,'move'+ k, {length : 0.7, label : 'hasMove', name : 'hasMove', directed : false});
	    			}
	    		}
	    	  //End render moves	*/
	    }
	};
	
	this.findAllMoves = function(term) {
		$.ajax( {
    		url: window.baseURL + '/findAllMoves',
    		data : {'term' : term },
    		dataType : 'json',
    		type:"POST",
    		success: function(data) {
    			$('#dance_data').hide();
    			$('#videoList').hide();
    			window.pageObj.renderFindAllMoves(data);    			
     		}
    	});
	};
	
	this.renderFindAllMoves = function(data) {
		
		sys = arbor.ParticleSystem();
		sys.parameters({stiffness:900, repulsion:3000, gravity:true, dt:0.015});
	    sys.renderer = Renderer($("#viewport"));
	    sys.addNode('dance', {'color':'#FFCC00', 'shape' : 'dot', 'label' : data['danceName'], 'type' : 'Dance'} );
	    
	    for(var i in data['moves']) {
	    	var children = data['moves'][i];
	    	for(var j in children) {
	    		sys.addNode(children[j], {'color':'#3399FF', 'shape' : 'rect', 'label' : children[j], 'type' : 'Move'});
	    		sys.addEdge('dance',children[j], {length : 0.5, label : 'hasMove', name : 'hasMove', directed : true, pointSize:3});

	    	}
	    }
	};
	
	this.nodeClicked = function(selected) {
		console.log(selected);
		var nodeType = selected.node.data.type;
		switch(nodeType) {
		
			case 'Dance':
				this.getDanceDetails(selected.node.data.label);
				break;
				
			case 'Move':
				this.getAllVideos(selected.node.data.label + ' in ' +sys.getNode('dance').data.label);
				break;
		}
	};
	
	
	var dance;
	this.nodeSingleClick = function(selected) {
	
		var nodeType = selected.node.data.type;
		if(window.pageObj.flag==0) dance=sys.getNode('dance').data.label;
		
		switch(nodeType) {
		
		case 'Dance':
			if(window.pageObj.flag==0) return;
			dance=selected.node.data.label;
			$.ajax( {
    		url: window.baseURL + '/findAllMoves',
    		data : {'term' : selected.node.data.label },
    		dataType : 'json',
    		type:"POST",
    		success: function(data) {
    			var moves=data['moves'];
	    		for(var j in moves){
	    			
	    			var submoves=moves[j];
	    			for(var k in submoves){
	    			sys.addNode(submoves[k], {'color':'#3399FF', 'shape' : 'rect', 'label' : submoves[k], 'type' : 'Move'});
	    			sys.addEdge(sys.getNode(selected.node.name),submoves[k], {length : 0.7, label : 'hasMove', name : 'hasMove', directed : false});
	    			}
	    		}
    			
     		}
    	});
		break;
		
		case 'Move':
	
			$.ajax( {
	    		url: window.baseURL + '/findAllMoves',
	    		data : {'term' : dance },
	    		dataType : 'json',
	    		type:"POST",
	    		success: function(data) {
	    			var moves=data['moves'];
		    		for(var j in moves){
		    			var submoves=moves[j];
		    			for(var k in submoves){
		    				
		    				
		    			if(submoves[k]==selected.node.data.label){
		    				sys.addNode('parentMove', {'color':'#00CC00', 'shape' : 'rect', 'label' :j,'type' : 'ParentMove'});
		    				for(var i in submoves){
		    					    if(sys.getNode(submoves[i])==null)continue;
		    						sys.addEdge(sys.getNode(submoves[i]),'parentMove', {length : 0.7, label : 'hasParentMove', name : 'hasParentMove', directed : false});
		    					}
				    		break;
		    			}	
		    		}
		    		}
	    			
	     		}
	    	});
			break;	
		
		}
	};
	
	this.loadedVideos = {};
	

	
	this.findCentrality = function(term1, nodeType1, term2, nodeType2){
		if(nodeType1=="dance")
			{	
				this.findCentrality_Dance(term1,term2);
			}
		
		if(nodeType1=="mm")
		{	
			this.findCentralityParentMoves(term1,term2);
		}
		
		if(nodeType1=="move")
		{	
			this.findCentrality_Moves(term1,term2);
		}
		
		if(nodeType1=="artist"){
			this.findCentrality_artist(term1,term2);
			
		}
	};
	
	
	
	this.findCentrality_Dance = function(term1,term2) {
		$.ajax({
    		url: window.baseURL + '/findCentralityDances',
    		data : {'term1' : term1,'term2':term2 },
    		dataType : 'json',
    		type:"POST",
    		success: function(data) {
    			$('#dance_data').hide();
    			$('#videoList').hide();
    			window.pageObj.renderFindCentality_Dance(data);    			
     		}
    	});
	};
	
	this.findCentrality_artist = function(term1,term2) {
		$.ajax({
    		url: window.baseURL + '/findCentralityArtists',
    		data : {'term1' : term1,'term2':term2 },
    		dataType : 'json',
    		type:"POST",
    		success: function(data) {
    			$('#dance_data').hide();
    			$('#videoList').hide();
    			window.pageObj.renderFindCentality_Dance(data);    			
     		}
    	});
	};
	
	this.findCentralityParentMoves = function(term1,term2) {
		$.ajax({
    		url: window.baseURL + '/findCentralityParentMoves',
    		data : {'term1' : term1,'term2':term2 },
    		dataType : 'json',
    		type:"POST",
    		success: function(data) {
    			$('#dance_data').hide();
    			$('#videoList').hide();
    			window.pageObj.renderFindCentality_Dance(data);    			
     		}
    	});
	};
	
	this.findCentrality_Moves = function(term1,term2) {
		$.ajax({
    		url: window.baseURL + '/findCentralityMoves',
    		data : {'term1' : term1,'term2':term2 },
    		dataType : 'json',
    		type:"POST",
    		success: function(data) {
    			$('#dance_data').hide();
    			$('#videoList').hide();
    			window.pageObj.renderFindCentality_Dance(data);    			
     		}
    	});
	};
	
	
	
	this.renderFindCentality_Dance= function(data){
		sys = arbor.ParticleSystem();
		sys.parameters({stiffness:900, repulsion:3000, gravity:true, dt:0.015});
	    sys.renderer = Renderer($("#viewport").css({"width":"900px","height":"600px"}));
	   
	    	sys.addNode(data['start'], {'color':'#FFCC00', 'shape' : 'dot', 'label' : data['start'], 'type' : data['type']} );
		    sys.addNode(data['end'], {'color':'#FFCC00', 'shape' : 'dot', 'label' : data['end'], 'type' : data['type']} );	    
	    
	    if(data['moves'])
	    {
	    	for(var i in data['moves']) {
	    		var child = data['moves'][i];
	    		sys.addNode(child['value'], {'color':'#3399FF', 'shape' : 'rect', 'label' : child['value'],'type' : 'Move'});
	    		sys.addEdge(data['start'],child['value'], {length : 0.7, label : 'hasMove', name : 'hasMove', directed : true});
	    		sys.addEdge(data['end'],child['value'], {length : 0.7, label : 'hasMove', name : 'hasMove', directed : true});
	    	}	
	    }
	    
	    if(data['categories'])
	    {
	    	for(var j in data['categories']) {
	    		var child = data['categories'][j];
	    		sys.addNode(child['value'], {'color':'#FF6699', 'shape' : 'dot', 'label' : child['value'],'type' : 'cat'});
	    		sys.addEdge(data['start'],child['value'], {length : 0.7, label : 'hasCategory', name : 'hasCategory', directed : true});
	    		sys.addEdge(data['end'],child['value'], {length : 0.7, label : 'hasCategory', name : 'hasCategory', directed : true});
	    	}	
	    }
	    
	    if(data['artists'])
	    {
	    	for(var k in data['artists']) {
	    		var child = data['artists'][k];
	    		sys.addNode(child['value'], {'color':'#00FF00', 'shape' : 'dot', 'label' : child['value'],'type' : 'artist'});
	    		sys.addEdge(data['start'],child['value'], {length : 0.7, label : 'hasArtist', name : 'hasArtist', directed : true});
	    		sys.addEdge(data['end'],child['value'], {length : 0.7, label : 'hasArtist', name : 'hasArtist', directed : true});
	
	    	}	
	    }
	    
	    if(data['mms'])
	    {
	    	for(var m in data['artists']) {
	    		var child = data['artists'][m];
	    		sys.addNode(child['value'], {'color':'#FF0000', 'shape' : 'rect', 'label' : child['value'],'type' : 'mmove'});
	    		sys.addEdge(data['start'],child['value'], {length : 0.7, label : 'hasMasterMove', name : 'hasMasterMove', directed : true});
	    		sys.addEdge(data['end'],child['value'], {length : 0.7, label : 'hasMasterMove', name : 'hasMasterMove', directed : true});
	
	    	}	
	    }
	    
	    if(data['beats'])
	    {
	    	for(var m in data['beats']) {
	    		var child = data['beats'][m];
	    		sys.addNode(child['value'], {'color':'#66FF33', 'shape' : 'rect', 'label' : child['value'],'type' : 'beat'});
	    		sys.addEdge(data['start'],child['value'], {length : 0.7, label : 'hasBeat', name : 'hasBeat', directed : true});
	    		sys.addEdge(data['end'],child['value'], {length : 0.7, label : 'hasBeat', name : 'hasBeat', directed : true});
	
	    	}	
	    }
	    
	    if(data['origins'])
	    {
	    	for(var m in data['origins']) {
	    		var child = data['origins'][m];
	    		sys.addNode(child['value'], {'color':'gray', 'shape' : 'dot', 'label' : child['value'],'type' : 'origin'});
	    		sys.addEdge(data['start'],child['value'], {length : 0.7, label : 'hasBeat', name : 'hasOrigin', directed : true});
	    		sys.addEdge(data['end'],child['value'], {length : 0.7, label : 'hasBeat', name : 'hasOrigin', directed : true});
	
	    	}	
	    	
	    	 
		    if(data['dances'])
		    {
		    	for(var m in data['dances']) {
		    		var child = data['dances'][m];
		    		sys.addNode(child['value'], {'color':'gray', 'shape' : 'dot', 'label' : child['value'],'type' : 'Dance'});
		    		sys.addEdge(data['start'],child['value'], {length : 0.7, label : 'hasDance', name : 'hasDance', directed : true});
		    		sys.addEdge(data['end'],child['value'], {length : 0.7, label : 'hasDance', name : 'hasDance', directed : true});
		
		    	}	
		    }
	    }
	    
	    	
	};
	
	this.getDanceDetails = function(term) {
		 //$('#dance_data').html('');
		   $.ajax({
		       url : window.baseURL + '/searchDance', 
		       type : 'POST',
		       data : {'term' : term},
		       datetype : 'json',
		       success : function(data) { 
		    	   if(data) {
		    		 $('#videoList').hide();
		  	         $('#dance_name1').html(data[0].name);
		  	         $('#dance_desc1').html(data[0].desc);
		  	         $('#dance_link1').attr('href',data[0].link);
		  	         $('#dance_link1').text(data[0].link);
		  	         $('#dance_image1').attr("src",data[0].thumbnail);
		  	         $('#dance_data').slideDown(1000);
		    	   }
		       }
		   });  
		   $('#dance_data').slideDown(1000);
	};     
	
	this.getAllVideos = function(term) {
	 $('#video_playlist').html('');
	   $.ajax({
	       url : window.baseURL + '/findMoveVideos', 
	       type : 'POST',
	       data : {'term' : term},
	       datetype : 'json',
	       success : function(data) { 
	       if(data['items']) {
	         var items = data['items'];
	         var content = '';

	         for(var i in items)  {
	           if(!window.pageObj.loadedVideos[items[i]['id']['videoId']]) {
	             content += "<div class='media'><a class='pull-left' href='#'> \
	             <img src='"+items[i]['snippet']['thumbnails']['default']['url']+"' rel='"+items[i]['id']['videoId']+"' alt='"+items[i]['snippet']['title']+"' class='img-thumbnail' /> \
	             </a> <div class='media-body'> <h4 class='media-heading'><a class='youtube' rel='"+items[i]['id']['videoId']+"' href='javascript:void(0)'>"+items[i]['snippet']['title']+"</a></h4> "+items[i]['snippet']['description']+"</div></div>";
	             window.pageObj.loadedVideos[items[i]['id']['videoId']] = items[i]['snippet']['title'];
	           }
	         };

	         $('#video_playlist').append(content);
	       }
	       $('#dance_data').hide();
	       $('#videoList').slideDown(1000);
	     }
	   });
	   $('body').delegate('a.youtube', 'click',function(evt){
	     $('#youtube_video').html('<iframe id="ytplayer" type="text/html" width="460px" height="349" src="http://www.youtube.com/embed/'+$(this).attr('rel')+'?autoplay=1&origin=http://example.com" frameborder="0"/>'); 
	     $("html, body").animate({ scrollTop: 0 }, "slow");          
	   // $('#ytplayer').attr('src', "http://www.youtube.com/embed/"+$(this).attr('rel')+"?autoplay=1&origin=http://example.com");
	   });

	   $('body').delegate('img.img-thumbnail', 'click',function(evt){
	     $('#youtube_video').html('<iframe id="ytplayer" type="text/html" width="460px" height="349" src="http://www.youtube.com/embed/'+$(this).attr('rel')+'?autoplay=1&origin=http://example.com" frameborder="0"/>'); 
	     $("html, body").animate({ scrollTop: 0 }, "slow");          
	   // $('#ytplayer').attr('src', "http://www.youtube.com/embed/"+$(this).attr('rel')+"?autoplay=1&origin=http://example.com");
	   });

	   $('#youtube_video').html('');
	   $('#youtube_video').slideDown(1000);
	};

	
	this.findClosesDance = function(term) {
		$.ajax({
			url : window.baseURL + '/closestdance', 
			type : 'POST',
			data : {'term' : term},
			datetype : 'json',
			success : function(data) { 
				$('#dance_data').hide();
    			$('#videoList').hide();
				window.pageObj.renderCloset(data);
			}
		});
	};
	
	this.renderCloset = function(data) {
		
		var labels = [];
		var values = [];
		for(var i in data) {
			labels.push(data[i]['key']);
			values.push(data[i]['value']);
		}
		dataset = [ {
			fillColor : "rgba(26,166,189,0.85)",
			strokeColor : "rgba(26,166,189,1)",
			pointColor : "rgba(220,220,220,1)",
			pointStrokeColor : "#00000",
			data : values
		}];
		$("#viewport").html('');
		var ctx2 = document.getElementById("viewport").getContext("2d");
		var g2 = new Chart(ctx2).Radar({'labels' : labels, 'datasets':dataset},
				{scaleLineColor : "rgba(0,0,0,0.4)",angleLineColor : "rgba(0,0,0,.4)", scaleShowLabels : true,
			pointLabelFontStyle : "bold"});
	};


}



$(document).ready(function(){
	
	window.pageObj = new Dance();
	pageObj.bindEvents();
	
});
