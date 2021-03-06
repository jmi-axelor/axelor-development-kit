/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2015 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
(function() {

"use strict";

var ui = angular.module('axelor.ui');

/**
 * The Boolean input widget.
 */
ui.formInput('Boolean', {
	
	css: 'boolean-item',
	
	cellCss: 'form-item boolean-item',
	
	link: function (scope, element, attrs, model) {
		
		var field = scope.field;
		var input = element.find('input');

		input.click(function () {
			scope.setValue(input[0].checked, true);
		});
	},
	template_readonly: null,
	template_editable: null,
	template:
		"<label class='ibox'>" +
			"<input type='checkbox' ng-model='record[field.name]' ng-disabled='isReadonly()'>" +
			"<span class='box'></span>" +
		"</label>"
});

/**
 * The Boolean widget with label on right.
 */
ui.formInput('InlineCheckbox', 'Boolean', {
	css: 'checkbox-inline',
	showTitle: false,
	link: function (scope, element, attrs, model) {
		this._super.apply(this, arguments);
		scope.$watch('attr("title")', function(title) {
			scope.label = title;
		});
	},
	template_readonly: null,
	template_editable: null,
	template:
		"<label class='ibox'>" +
			"<input type='checkbox' ng-model='record[field.name]' ng-disabled='isReadonly()'>" +
			"<div class='box'></div>" +
			"<span class='title'>{{label}}</span>" +
		"</label>"
});

ui.formInput('Toggle', 'Boolean', {
	cellCss: 'form-item toggle-item',
	link: function (scope, element, attrs, model) {
		this._super.apply(this, arguments);

		var field = scope.field;
		var icon = element.find('i');

		scope.icon = function () {
			return model.$viewValue && field.iconActive ? field.iconActive : field.icon;
		};

		scope.toggle = function () {
			var value = !model.$viewValue;
			if (scope.setExclusive && field.exclusive) {
				scope.setExclusive(field.name, scope.record);
			}
			scope.setValue(value, true);
		};

		if (field.help || field.title) {
			element.attr('title', field.help || field.title);
		}
	},
	template_editable: null,
	template_readonly: null,
	template:
		"<button tabindex='-1' class='btn btn-default' ng-class='{active: record[field.name]}' ng-click='toggle()'>" +
			"<i class='fa {{icon()}}'></i>" +
		"</button>"
});

ui.formInput('BooleanSelect', 'Boolean', {
	css: 'form-item boolean-select-item',
	init: function (scope) {
		var field = scope.field;
		var trueText = (field.widgetAttrs||{}).trueText || _t('Yes');
		var falseText = (field.widgetAttrs||{}).falseText || _t('No');

		scope.$selection = [trueText, falseText];
		scope.format = function (value) {
			return value ? scope.$selection[0] : scope.$selection[1];
		};
	},
	link_editable: function (scope, element, attrs, model) {
		var input = element.find('input');
		var items = scope.$selection;

		input.autocomplete({
			minLength: 0,
			source: items,
			select: function (e, u) {
				var value = items.indexOf(u.item.value) === 0;
				scope.setValue(value, true);
				scope.applyLater();
			}
		}).click(function (e) {
			input.autocomplete("search" , '');
		});

		scope.$render_editable = function () {
			var value = model.$viewValue || false;
			var current = items.indexOf(input.val()) === 0;
			value ? input.val(items[0]) : input.val(items[1]);
		};

		scope.$watch('isReadonly()', function (readonly) {
			input.autocomplete(readonly ? "disable" : "enable");
			input.toggleClass('not-readonly', !readonly);
		});
	},
	template: "<span class='form-item-container'></span>",
	template_readonly: '<span>{{text}}</span>',
	template_editable: "<span class='picker-input'>" +
				"<input type='text' readonly='readonly'>" +
				"<span class='picker-icons picker-icons-1'>" +
					"<i class='fa fa-caret-down'></i>" +
				"</span>" +
			"</span>"
});

ui.formInput('BooleanRadio', 'BooleanSelect', {
	css: 'form-item boolean-radio-item',
	link_editable: function (scope, element, attrs, model) {

		var inputName = _.uniqueId('boolean-radio');
		var trueInput = $('<input type="radio" data-value="true" name="' + inputName + '">');
		var falseInput = $('<input type="radio" data-value="false" name="' + inputName + '">');

		var items = scope.$selection;

		$('<label class="ibox round">')
			.append(trueInput)
			.append($('<i class="box">'))
			.append($('<span class="title">').text(items[0]))
			.appendTo(element);
		$('<label class="ibox round">')
			.append(falseInput)
			.append($('<i class="box">'))
			.append($('<span class="title">').text(items[1]))
			.appendTo(element);

		scope.$render_editable = function () {
			var value = model.$viewValue || false;
			var input = value ? trueInput : falseInput;
			input.attr('checked', true);
		};

		element.on('change', 'input', function (e) {
			var value = $(this).data('value') === true;
			scope.setValue(value, true);
			scope.applyLater();
		});
	},
	template_editable: "<span></span>"
});

ui.formInput('BooleanSwitch', 'Boolean', {
	css: 'form-item',
	template_readonly: null,
	template_editable: null,
	template:
		"<label class='iswitch'>" +
			"<input type='checkbox' ng-model='record[field.name]' ng-disabled='isReadonly()'>" +
			"<span class='box'></span>" +
		"</label>"
});

})();
