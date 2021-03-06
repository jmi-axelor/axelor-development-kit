/**
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
package com.axelor.db.mapper.types;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

import com.axelor.db.Model;
import com.axelor.db.mapper.Mapper;
import com.axelor.db.mapper.TypeAdapter;

public class MapAdapter implements TypeAdapter<Map<?, ?>> {

	@SuppressWarnings("unchecked")
	@Override
	public Model adapt(Object value, Class<?> type, Type genericType, Annotation[] annotations) {
		if (value instanceof Model)
			return (Model) value;
		return Mapper.toBean((Class<Model>) type, (Map<String, Object>) value);
	}
}
