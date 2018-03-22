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
            $tempInput.attr('name', name).addClass('inputFiles');
            $("#singleFiles").append($tempInput);
        }).click();

        $("#sendTranBtn").click(function(){
            var datas = {
                outputFilePath : $('#outputFolderPath').val(),
                inputType : $('input[name="inputType"]:checked').val(),
                inputFolderPath : $('#inputFolderPath').val(),
                inputFilesPath : (function(){
                    var result = "";
                    $('input[class*="inputFiles"]').each(function(){
                        if('' !== this.value)
                            result += this.value + ",";
                    });
                    result = result.substring(0, result.lastIndexOf(","));
                    return result;
                })()
            };

            $.ajax({
                type: 'post',
                async: true,
                url: url('demoimageutilhandler/demo'),
                data: datas,
                success: function(res){
                    $('#resultBoard').val(res.result);
                }
            });
        });

        $("#sendBase64Btn").click(function(){
            var datas = {
                inputFilePath : $('#transBase64InputFilePath').val()
            };
            
            $.ajax({
                type: 'post',
                async: true,
                url: url('demoimageutilhandler/demoBase64'),
                data: datas,
                success: function(res){
                    $('#resultBoard').val(res.result);
                }
            });
        });

    });
});
