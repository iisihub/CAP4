require.config({
  //all js setting
  paths : {
    'json3' : 'lib/js/json3.min',
    'socketio' : 'lib/js/socket.io',
    'jquery' : 'lib/js/jquery/jquery-3.1.1.min',
    'jquery-ui' : 'lib/js/jquery/ui/js/jquery-ui-1.12.1.fix.deprecated',
    'jqgrid-i18n' : 'lib/js/jquery/plugin/jquery.jqGrid-4.5.4/js/i18n/grid.locale-tw',
    'jqgrid' : 'lib/js/jquery/plugin/jquery.jqGrid-4.5.4/js/jquery.jqGrid.fix.deprecated',
    'validate-i18n' : 'lib/js/jquery/plugin/formValidator/languages/jquery.validationEngine-zh_TW',
    'validate' : 'lib/js/jquery/plugin/formValidator/jquery.validationEngine',
    'fileupload' : 'lib/js/jquery/plugin/ajaxfileupload/ajaxfileupload',
    'blockui' : 'lib/js/jquery/plugin/blockUI/jquery.blockUI',
    'underscore' : 'lib/js/underscore/1.8.3/underscore',
    'backbone' : 'lib/js/backbone/1.3.3/backbone',
    'sceditor' : 'lib/js/jquery/plugin/sceditor/jquery.sceditor.xhtml.min',
    'ifvisible' : 'lib/js/ifvisible/ifvisible', /* idle handle */
    'timer' : 'lib/js/timer/jquery.timer', /* timer handle */
    'fullcalendar' : 'lib/js/calendar/fullcalendar'
  },
  shim : {
    'blockui' : [ 'jquery' ],
    'jquery-ui' : [ 'jquery' ],
    'jqgrid-i18n' : [ 'jquery' ],
    'jqgrid' : [ 'jquery-ui', 'jqgrid-i18n' ],
    'validate-i18n' : [ 'jquery' ],
    'validate' : [ 'jquery', 'validate-i18n' ],
    'fileupload' : [ 'jquery' ],
    'backbone' : [ 'underscore', 'jquery' ],
    'sceditor' : [ 'jquery' ],
    'ifvisible' : [ 'jquery' ],
    'timer' : [ 'jquery' ],
    'fullcalendar' : [ 'jquery' ]
  }
});

define('libjs', [ 'json3', 'socketio', 'jquery', 'blockui', 'jquery-ui', 'jqgrid-i18n', 'jqgrid', 'validate-i18n', 'validate', 'fileupload', 'underscore', 'backbone', 'sceditor', 'ifvisible',
    'timer', 'fullcalendar'], function() {
  window.ifvisible = require.s.contexts._.defined.ifvisible;
  // $.holdReady(true); // enable at common.js
  logDebug('lib init');
});
