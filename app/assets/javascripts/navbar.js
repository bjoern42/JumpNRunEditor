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

}

function downloadLevel(){
    var url = jsRoutes.controllers.Editor.download().url;
    window.location = url;
}

function createConfirmDialog(title, message){
    var dialog = $('<div data-role="dialog" id="confirm_dialog" title="'+title+'">'+message+'</div>');
    return dialog;
}