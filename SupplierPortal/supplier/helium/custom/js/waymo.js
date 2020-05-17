(function(App, $) {
	var redirectTo = {
		changeAction : function() {
			App.routing.entry('tvc:page:helium/ChangeActions.xml');
		},

		tasks : function() {
			App.routing.entry('tvc:page:helium/AssignedTasks.xml');
		}
	};
	
	var taskOperation = {
			createField : function(form, label, value){
				var itemId = label.replace(/\s+/g, '_');
				var itemSchema = {
					"type": "string",
				};
				var itemOptions = {
					"label": label,
					"fieldClass":"field"
				};
				form.formControl.addItem(itemId, itemSchema, itemOptions, value, null);
				return $("[name='" + itemId +"']");
			},
			objIds : function(that){
				var data = that.selectedRows();
				if (data.length === 0) {
					App.toaster.error("Please select any row");
					return [];
				}
				var objectIds = [];
				for ( var i = 0; i < data.length; i++) {
					objectIds.push(data[i].objectId);
				}
					
				return objectIds;
			},
			postRender : function() {
				var form = this;
				if (form.element) {
					form.element.width(400);
					form.element.height(500);
				}
				
				form.fieldObject = taskOperation.createField(form, "Comments", "");
				
				$(".alpaca-form-button-submit").off('click');
				$('.alpaca-form-button-submit').click(function(event){
					event.preventDefault();
					App.waymo.submitTaskParameters(form);
					$("a.close").click();
					App.page.refreshWidgetById("assignedtasks");
				});
			},
			operation : function(input) {
				var objectIds = taskOperation.objIds(this);
				if (objectIds && objectIds.length != 0){
					var options = {
						options : {
							formConfigName : 'tvc:form:helium:tasks/TaskActions.xml',
							fullscreen : true,
							formMode : 'EDIT'
						},
						postRender : 'App.waymo.taskOperation.postRender'
					};
					
					var form = App.form.create(options);
					
					form.objectIds = objectIds;
					form.operation = input.id;
					return form;
				}
				return null;
			}
	};
	
	var submitTaskParameters = function(form) {
		var objectIds = form.objectIds;
		var operation = form.operation;
		var comments = form.fieldObject.val();
		var url = App.info.actionURL + 'waymoTaskActions';
		var params = $.extend(true, {}, {
			objectIds : objectIds,
			operation : operation,
			comments : comments
		}, {});
		return $.ajax({
			url: url,
			type: 'GET',
			data: params,
			async : false,
			traditional : true
		}, false).then(function(message){
			if(message === 'approve'){
				App.toaster.success("Approved/Completed Successfully");
			} else if(message === 'reject'){
				App.toaster.success("Rejected Successfully");
			}else{
				App.toaster.error(message);
			}
		});
	};
	
	var startDiscussion = function(value) {
		var objectIds = taskOperation.objIds(this);
		if (objectIds && objectIds.length != 0){
			var collaborator  = top.getCollaborator();
			collaborator.openCollaborationPanel([ objectIds[0] ], {
				component : 'discussion'
			});
		}
	};
	
	
		function getSelectedObjectIds(data) {
		if (data.length === 0) {
			return;
		}
		var objectIds = [];
		for (var i = 0; i < data.length; i++) {
			objectIds.push(data[i].objectId);
		}
		return objectIds;
    }
	

	var tagAsImplemented = function(input) {
		var contextOid = App.page.getObjectId();
		var objectIds = getSelectedObjectIds(this.selectedRows());		
		if(objectIds=="undefined" || objectIds==undefined){
			alert("Please select one or more Affected Items to Tag as Implemented.");
		}else{
			var popupURL = "../../../waymo/goog_TagSelectedPartsAsImplemented.jsp?contextObjId="+contextOid+"&objectId="+objectIds;
			var popup = window.open(popupURL);			
		}
		var widget = this instanceof App.Widget ? this : this.widget;
		
		
		

	};
	
    var lockTableScrollbar = function() {
        var widget = this.widget,
            dataTableEl = this.element;
 
        if (!(dataTableEl instanceof $)) {
            return;
        }
 
        widget.$element.addClass("widget-locked-scrollbar");
 
        var setScrollHandler = function(dataTableEl) {
            var $headerTable = dataTableEl.find(".he-fixed-header-wrapper > table");
 
            dataTableEl
                .find(".widget-inner").off("scroll.lockTableScrollbar")
                .on("scroll.lockTableScrollbar", function(e) {
                    $headerTable.css("left", e.target.scrollLeft * -1);
                });
        };
 
        setScrollHandler(dataTableEl);
 
        widget.on("fullscreen", function() {
            setScrollHandler(widget.modal.$element.addClass("widget-locked-scrollbar"));
        });
    };
	
	App.waymo = App.waymo || {};
	App.waymo.redirectTo = redirectTo;
	App.waymo.taskOperation = taskOperation;
	App.waymo.submitTaskParameters = submitTaskParameters;
	App.waymo.startDiscussion = startDiscussion;
	App.waymo.tagAsImplemented = tagAsImplemented;
	App.waymo.lockTableScrollbar = lockTableScrollbar;
})(window.App || {}, jQuery);