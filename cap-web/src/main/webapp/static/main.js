var logDebug = function() {
  if (window.console) {
    console.log(arguments.length > 0 ? arguments[0] : arguments);
  }
};
var baseUrl = baseUrl || '../static';
require.config({
  urlArgs : 'cache=' + new Date().getTime(),
  baseUrl : baseUrl,
  paths : {
    'jquery-ui' : 'lib/js/jquery/ui/js/jquery-ui-1.12.1.fix.deprecated',
    'libjs' : 'lib/js/libjs', // third party lib
    'capjs' : 'lib/js/capjs', // cap lib
    'common.properties' : 'lib/js/common/common.properties',
    'cust-properties' : 'js/common/cust.properties', // 客制化設定檔
    'cust-common' : 'js/common/cust.common' // 客制化 CommonJS檔
  },
  shim : {
    'cust-properties' : [ 'libjs', 'common.properties' ],
    'capjs' : [ 'libjs', 'cust-properties' ],
    'cust-common' : [ 'jquery-ui', 'libjs', 'common.properties', 'capjs' ]
  // 客制化lib 載入設定
  }
});

require([ 'jquery-ui', 'libjs', 'common.properties', 'cust-properties', 'capjs', 'cust-common' ], function() {
//  console.debug("cust js init");
});

// global method
window.loadScript = function(url) {
  require([ 'cust-common' ], function() {
    require([ url ], function(pageJs) {
      logDebug(url + ' loaded');
      pageJs && pageJs.init();
    })
  });
};
window.pageInit = function(settings) {
  if (settings) {
    define.call(window, [ 'cust-common' ], function() {
      return settings instanceof Function ? {
        init : settings
      } : settings;
    });
  }
  // add PagInit control checkTimeout
  if (Properties.remindTimeout) {
    window.CCPAGENO = 'newcrd' + parseInt(Math.random() * 1000, 10);
    $.ajax({
      url : url('checktimeouthandler/checkTO'),
      type : 'post',
      data : {
        CCPAGENO : window.CCPAGENO
      },
      success : function(res) {
      }
    });
  }
};
