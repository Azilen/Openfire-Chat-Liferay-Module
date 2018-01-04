<%@ include file="init.jsp" %>

<portlet:resourceURL var="chatAPI" id="/chatAPI">
	<portlet:param name="requestCommand" value="creategroup"></portlet:param>
</portlet:resourceURL>

	<div class="main-header">
		<nav class="navbar navbar-inverse navbar-fixed-top">
			<div class="container-fluid">
				<div class="container">
					<div class="navbar-header">
						<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
							<span class="sr-only">Toggle navigation</span>
							<span class="icon-bar"></span>
							<span class="icon-bar"></span>
							<span class="icon-bar"></span>
						</button>
						<a class="navbar-brand" href="#">
                            <img src='<%=request.getContextPath() + "/images/ChatRoomLogo.png"%>' alt="logo" class="logo-img">
                            <h4>CHAT GROUP</h4>
                        </a>
					</div>
				</div>
			</div>
		</nav>
	</div>
	<section class="chat-section">
		<div id="candy"></div>
	</section>

	<!-- Add Group Modal -->
	
	<div class="modal fade text-center cr-modal" role="dialog" id="addGroupModal">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
					<h4 class="modal-title" id="myModalLabel">Add New Group</h4>
				</div>
				<div class="modal-body">
					<form id="addNewGropuForm" action="">
						<div class="form-group">
							<input type="text" name="group_name" id="group_name" class="form-control text-center" placeholder="Group Name">
							<label class="error-cr" id="errorMsg"></label>
						</div>
						<div class="form-group">
							<select class="form-control text-center allUsersForGroup" name="users[]" multiple="multiple"></select>
							<label class="error-cr" id="usererrorMsg"></label>
						</div>
						<div class="form-group">
							<button type="submit" class="btn btn-primary btn-theme-cr">Add Group</button>
							<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
	<!-- Add Group Modal Ends-->

	<!-- Delete Group Modal -->
	<div class="modal fade text-center cr-modal" role="dialog" id="deleteGroupModal">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
					<h4 class="modal-title" id="myModalLabel">Delete Group</h4>
				</div>
				<div class="modal-body">
					<div class="form-group">
						<input type="hidden" id="deleteGroupId">
						<h4>Delete this group?</h4>
					</div>
					<div class="form-group">
						<button type="button" class="btn btn-danger deleteGroupBtn">Delete</button>
						<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- Delete Group Modal Ends-->

<link rel="stylesheet" href="https://www.jqueryscript.net/demo/jQuery-Plugin-For-Filterable-Multiple-Select-with-Checkboxes-fSelect/fSelect.css" />

<script src="http://netdna.bootstrapcdn.com/bootstrap/3.0.2/js/bootstrap.min.js"></script>
<!-- Dropdown Js and Css -->
<script src="https://www.jqueryscript.net/demo/jQuery-Plugin-For-Filterable-Multiple-Select-with-Checkboxes-fSelect/fSelect.js"></script>


<script type="text/javascript">
(function ($) {
	
    $(document).ready(function () {
    	 initializeCandy();
    	 
    	 setTimeout(function(){
    		 removeDeleteFromGeneral(); 
    	 }, 2000);
    });
    
    function initializeCandy(){
    	Candy.init('${xmpp_HTTP_BIND_URL}', { // uncomment & comment next line if you'd like to use BOSH
  	      // Candy.init('ws://localhost:5280/xmpp-websocket/', {
  	        core: {
  	          // only set this to true if developing / debugging errors
  	          debug: false,
  	          // autojoin is a *required* parameter if you don't have a plugin (e.g. roomPanel) for it
  	          //   true
  	          //     -> fetch info from server (NOTE: does only work with openfire server)
  	          //   ['test@conference.example.com']
  	          //     -> array of rooms to join after connecting
  	          //autojoin: ['general@conference.'+'${xmppDomainName}','group1@conference.'+'${xmppDomainName}']
  	          autojoin: [${xmpp_USER_CHATROOMS}]
  	        },
  	        view: { assets: 'res/' }
  	      });

	  	var  isXmppConnected = 'isXmppConnected';
	  	console.log(isXmppConnected);
	  	if(isXmppConnected || isXmppConnected=='true'){
	  		//Candy.Core.connect('${user.emailAddress}', '${xmpp_token}');
	  		Candy.Core.attach('${jid}', '${sid}', '${rid}');
	  	}else{
	  		alert('Connection error with XMPP Server');
	  	}
    }

   	function removeDeleteFromGeneral(){
	  	/* Remove delete option from General(Primary group) */
	  	$( "h5:contains('general')" ).next("span").remove();	  	
	}
		
	$("#addGroupModal").hide();
	$("#deleteGroupModal").hide();
	
	/* Opens Add group dialog with preloaded open-fire users */
    $(document).on('click', '.addGroupModal',function(){
    	$('#errorMsg').html("");
    	$('#usererrorMsg').html("");
    	
    	$.ajax({
			url: '${xmpp_HTTP_REST_URL}'+'plugins/restapi/v1/users',
			dataType: 'json',
			type:'GET',
			accept:'application/json',
			beforeSend: function (xhr){ 
		        xhr.setRequestHeader('Authorization', '${xmppOpenfireAuthToken}'); 
		    },
			success: function(result){
				$(".allUsersForGroup").html("");
				if(result.user.length > 0){
					for(var i=0;i<result.user.length;i++){
						var obj = result.user[i];
						$(".allUsersForGroup").append("<option value='"+obj.username+"'>"+obj.username+"</option>");
					}
					$('.allUsersForGroup').fSelect();
				}
			},
			error: function(msg){
				console.log(msg);
			}
		});
    	
    	$("#addGroupModal").modal('show');
	});
	
	/* submit Add group form */
	$('#addNewGropuForm').on('submit', function(e) {
		e.preventDefault();
		if ($('#group_name').val().trim() == "") {
			$('#errorMsg').html('Please enter the Group Name.');
		}
		
		if($('.allUsersForGroup').val() == "" || $('.allUsersForGroup').val() == null){
			$('#usererrorMsg').html('Please select any user.');
		}

		var apiUrl = '<%=chatAPI.toString() %>';
		
		var selectedusers = [];
        $('.allUsersForGroup :selected').each(function(i, selected) {
        	selectedusers[i] = $(selected).val();
        });
        
		$.ajax({
			url: apiUrl,
			data:{
				'${ns}groupName': $('#group_name').val(),
				'${ns}memberList': $('select[name="users[]"]').val()
			},
			success: function(result){
				console.log('success:' + result);
				alert("Group Created successfully");
				location.reload();
			},
			error: function(error){
				$("#addGroupModal").modal('show');
				alert("Group can not be Created, " + error.message);
			}
		});
	   
	});
	
	/* Openes Delete group popup */
	$(document).on('click', '.deleteGroupModal',function(){
		$("#deleteGroupId").val($(this).attr("data-id"));
		$("#deleteGroupModal").modal('show');
	});
	
	/* Delete group API call*/
	$(".deleteGroupBtn").on("click", function(){
		var groupIdToDelete = $("#deleteGroupId").val();
		$.ajax({
			url: '${xmpp_HTTP_REST_URL}'+'plugins/restapi/v1/chatrooms/' + groupIdToDelete,
			type:'DELETE',
			beforeSend: function (xhr){ 
		        xhr.setRequestHeader('Authorization', '${xmppOpenfireAuthToken}'); 
		    },
			success: function(result){
				$("#deleteGroupModal").modal('hide');
				alert("Group Deleted Succesfully");
				location.reload();
			},
			error: function(error){
				$("#deleteGroupModal").modal('hide');
				alert("Group can not be Deleted, Please contact your administrator");
			}
		});
	});
	
})(jQuery);
    
    
</script>
