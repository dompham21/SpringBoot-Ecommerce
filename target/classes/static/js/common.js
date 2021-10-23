function showImageThumbnail(fileInput) {
    let file = fileInput.files[0];
    let reader = new FileReader();

    reader.onload = function(e) {
        $("#thumbnail").attr("src", e.target.result);
    };

    reader.readAsDataURL(file);
}
function checkEmailUnique(form) {
    url = "[[@{/users/check_email}]]";
    csrf = $("input[name='_csrf']").val();
    userEmail = $("#email").val();
    userId = $("#id").val();
    params = {id: userId, email: userEmail,_csrf: csrf}



    $.post(url, params, function(response) {
        if(response == "Ok") {
            form.submit();
        }
        else if(response == "Duplicated") {
            showModalDialog("Warning", "There is another user having the email " + userEmail)
        }

    })
    return false;

}

function showModalDialog(title, message) {
    $("#modalTitle").text(title);
    $("#modalBody").text(message);
    $("#modalDialog").modal();
}

function showModalConfirm(title, message) {
    $("#confirmTitle").text(title);
    $("#confirmText").text(message);
    $("#confirmModal").modal();
}