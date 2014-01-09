function newLevel(){
    var dialog = createConfirmDialog("Create New Level", "Are you sure?");
    dialog.dialog({
        resizable: false,
        modal: true,
        buttons:{
            OK: function () {
                var url = jsRoutes.controllers.Editor.new_level().url;
                window.location = url;
                $(this).dialog("close");
                $(this ).remove();
            },
            CANCEL: function(){
                $(this).dialog("close");
                $(this ).remove();
            }
        }
    });
}

function uploadLevel(){
    var dialog = createUploadDialog();
    console.log(dialog);
    dialog.dialog({
        resizable: false,
        modal: true,
        buttons:{
            CANCEL: function(){
                $(this).dialog("close");
                $(this ).remove();
            }
        }
    });
}

function logout(){
    var url = jsRoutes.controllers.Application.logout().url;
    window.location = url;
}

function downloadLevel(){
    var url = jsRoutes.controllers.Editor.download().url;
    window.location = url;
}

function createConfirmDialog(title, message){
    var dialog = $('<div data-role="dialog" id="confirm_dialog" title="'+title+'">'+message+'</div>');
    return dialog;
}

function createUploadDialog(){
    var action = jsRoutes.controllers.Editor.upload().url;
    var dialog = $('<div data-role="dialog"><form action="'+action+'" method="POST" enctype="multipart/form-data"><input type="file" name="level"><br><p><input type="submit" value="upload"></p></form></div>');
    return dialog;
}