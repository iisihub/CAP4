pageInit(function() {
    $(document).ready(function() {
        var $resultBoard = $('#resultBoard'),
            $address = $("#address");


        $("#sendData").click(function(e) {
            e.preventDefault();
            var datas = {
                address : $address.val(),
            };
            $.ajax({
                type : 'post',
                async : true,
                url : url('demoaddresshandler/testAddressNormal'),
                data : datas,
                success : function(res) {
                    $resultBoard.val(res.result);
                }
            });
        });

    });
});
