window.addEventListener('DOMContentLoaded', function() {
  'use strict'
  var model = {
    _canvas : null,
    _isDrawing : false,
    _isClean : true,
    _lineColor : 'black',
    _lineWidth : 1.5,
    /**
     * 檢查 fancybox 是否開啟
     */
    _checkPopupOpen : function() {
      return $.fancybox.isOpen;

    },
    /**
     * 檢查簽名是否超出 Canvas 區域
     * 
     * @param {number}
     *          userPositionX 輸入位置（頁面）水平座標
     * @param {number}
     *          userPositionY 輸入位置（頁面）垂直座標
     */
    _checkIsOverRegion : function(userPositionX, userPositionY) {
      var xOnCanvas = userPositionX - model._canvas.offsetLeft - model._canvas.style.borderWidth;
      var yOnCanvas = userPositionY - model._canvas.offsetTop - model._canvas.style.borderWidth;

      return ((xOnCanvas < 0 || xOnCanvas > model._canvas.width) || (yOnCanvas < 0 || yOnCanvas > model._canvas.height));

    },
    _initCanvas : function() {
      var canvasContext = model._canvas.getContext('2d');

      // 將 Canvas 背景填滿白色
      canvasContext.fillStyle = 'white';
      canvasContext.fillRect(0, 0, mainCanvas.width, mainCanvas.height);
      // 線條設定為 1.5 倍
      canvasContext.lineWidth = 1.5;

    },
    prepareToDraw : function(canvas, userPositionX, userPositionY) {
      if (model._checkPopupOpen()) {
        return;

      }

      if (model._isClean) {
        model._canvas = canvas;

        model._initCanvas();

      }

      model._isDrawing = true;

      var canvasContext = model._canvas.getContext('2d');

      canvasContext.beginPath();
      canvasContext.moveTo(userPositionX - model._canvas.offsetLeft, userPositionY - model._canvas.offsetTop);

    },
    stopDraw : function() {
      model._isDrawing = false;

    },
    draw : function(userPositionX, userPositionY) {
      if (!model._isDrawing) {
        return;

      }

      var canvasContext = model._canvas.getContext('2d');

      if (model._checkIsOverRegion(userPositionX, userPositionY)) {
        model.cleanDraw(true);

      } else {
        canvasContext.lineTo(userPositionX - model._canvas.offsetLeft, userPositionY - model._canvas.offsetTop);
        canvasContext.stroke();

        model._isClean = false;

      }

    },
    cleanDraw : function(isShowAlert) {
      if (model._canvas === null) {
        return;

      }

      var canvasContext = model._canvas.getContext('2d');

      model.stopDraw();

      canvasContext.clearRect(0, 0, model._canvas.width, model._canvas.height);

      model._isClean = true;

      if (isShowAlert) {
        view.showOverRegionAlert();

      }

    },
    /**
     * 開啟或關閉 fancybox
     */
    switchPopup : function(isOn) {
      // 一率先嘗試關閉 fancybox，避免 $.fancybox.isOpen 沒有正確偵測
      $.fancybox.close();

      if (isOn) {
        $.fancybox.open({
          href : '#msg',
          type : 'inline',
          height : '30',
          width : '30',
          autoSize : false,
          closeBtn : false,
          scrollOutside : false,
          helpers : {
            overlay : {
              closeClick : false
            }
          }
        });

      }

    },
    downloadCanvasImg : function() {
      if (model._isClean) {
        return;

      }

      var downloadUrl = model._canvas.toDataURL('image/png');

      var downloadLink = document.createElement('a');
      downloadLink.href = downloadUrl;
      downloadLink.download = 'signature.png';

      downloadLink.click();

    }
  };

  var view = {
    mainCanvas : document.getElementById('mainCanvas'),
    cleanCanvasBtn : document.getElementById('cleanCanvasBtn'),
    getCanvasImgBtn : document.getElementById('getCanvasImgBtn'),
    showOverRegionAlert : function() {
      alert('請於白色方框內簽名！');

    }
  };

  var controller = {
    checkIsLandscape : function() {
      var query = window.matchMedia('(orientation: landscape)');

      model.switchPopup(!query.matches);

    },
    startDrawing : function(event) {
      event.preventDefault();
      var targetCanvas = event.target;
      var userX;
      var userY;

      var eventType = event.type;

      switch (eventType) {
      case 'touchstart':
        userX = event.touches.item(0).pageX;
        userY = event.touches.item(0).pageY;

        model.prepareToDraw(targetCanvas, userX, userY);

        break;

      case 'mousedown':
        userX = event.pageX;
        userY = event.pageY;

        model.prepareToDraw(targetCanvas, userX, userY);

        break;

      }

    },
    stopDrawing : function() {
      model.stopDraw();

    },
    drawing : function(event) {
      event.preventDefault();

      var userX;
      var userY;

      var eventType = event.type;

      switch (eventType) {
      case 'touchmove':
        userX = event.touches.item(0).pageX;
        userY = event.touches.item(0).pageY;

        model.draw(userX, userY);

        break;

      case 'mousemove':
        userX = event.pageX;
        userY = event.pageY;

        model.draw(userX, userY);

        break;

      }

    },
    cleanDrawing : function() {
      model.cleanDraw();

    },
    getSignatureImg : function() {
      model.downloadCanvasImg();

    }
  };

  (function pageInit() {
    // window.addEventListener('orientationchange', function () {
    // controller.checkIsLandscape();

    // });

    var portraitOrientationCheck = window.matchMedia("(orientation: portrait)");
    portraitOrientationCheck.addListener(function() {
      controller.checkIsLandscape();

    });

    view.mainCanvas.addEventListener('touchstart', function(event) {
      controller.startDrawing(event);

    });

    view.mainCanvas.addEventListener('touchend', function() {
      controller.stopDrawing();

    });

    view.mainCanvas.addEventListener('touchmove', function(event) {
      controller.drawing(event);

    });

    view.mainCanvas.addEventListener('mousedown', function(event) {
      controller.startDrawing(event);

    });

    view.mainCanvas.addEventListener('mouseup', function() {
      controller.stopDrawing();

    });

    view.mainCanvas.addEventListener('mouseout', function() {
      controller.cleanDrawing();

    });

    view.mainCanvas.addEventListener('mousemove', function(event) {
      controller.drawing(event);

    });

    view.cleanCanvasBtn.addEventListener('click', function() {
      controller.cleanDrawing();

    });

    view.getCanvasImgBtn.addEventListener('click', function() {
      controller.getSignatureImg();

    });

    // 頁面載入完畢即檢查螢幕方向
    controller.checkIsLandscape();

  })();

});