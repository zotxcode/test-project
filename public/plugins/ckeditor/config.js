/**
 * @license Copyright (c) 2003-2017, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function( config ) {
	// Define changes to default configuration here. For example:
	// config.language = 'fr';
	// config.uiColor = '#AADC6E';
	config.extraPlugins = 'button';
	config.extraPlugins = 'toolbar';
	config.extraPlugins = 'notification';
	config.extraPlugins = 'notificationaggregator';
	config.extraPlugins = 'filetools';
	config.extraPlugins = 'widgetselection';
	config.extraPlugins = 'clipboard';
	config.extraPlugins = 'lineutils';
	config.extraPlugins = 'widget';
	config.extraPlugins = 'uploadwidget';
	config.extraPlugins = 'uploadimage';
	config.imageUploadUrl = '/admin/ckeditor/upload';
};

CKEDITOR.config.toolbar = [
    ['Styles','Format','Font','FontSize'],
    '/',
    ['Bold','Italic','Underline','StrikeThrough','-','Undo','Redo','-','Cut','Copy','Paste','Find','Replace','-','Outdent','Indent','-','Print'],
    '/',
    ['NumberedList','BulletedList','-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],
    ['Table','-','Link','Image','Smiley','TextColor','BGColor','Source']
] ;
