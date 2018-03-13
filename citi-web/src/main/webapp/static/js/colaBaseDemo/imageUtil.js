pageInit(function () {
    $(document).ready(function () {
        var fileCount = 0;
        $("#addFileBtn").click(function () {
            fileCount++;
            if (fileCount > 5) {
                alert("max files is 5!");
                return;
            }
            var $tempInput = $("#inputTemplate").find("[name='inputFile']").clone();
            var name = $tempInput.attr('name') + fileCount;
            $tempInput.attr('name', name);
            $("#singleFiles").append($tempInput);
        }).click();

        $("#sendBase64Btn").click(function(){
            $.ajax({
                type: 'post',
                async: true,
                url: url('demoimageutilhandler/demo'),
                data: {
                    test: '123'
                }
            });
        });

    });
});
