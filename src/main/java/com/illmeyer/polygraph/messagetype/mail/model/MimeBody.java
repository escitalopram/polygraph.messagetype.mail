/*
This file is part of the Polygraph bulk messaging framework
Copyright (C) 2012 Wolfgang Illmeyer

The Polygraph framework is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package com.illmeyer.polygraph.messagetype.mail.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class MimeBody implements Body, Serializable {

	private static final long serialVersionUID = 8045096708044600288L;
	/**
	 * Mime Subtype of this aggregate, e.g. mixed, alternative,...
	 */
	private String subType;
	/**
	 * elements of this aggregate
	 */
	private final List<Body> subElements = new ArrayList<>();
	/**
	 * list of additional/changed headers for this mime aggregate
	 */
	private final List<HeaderEdit> headers = new ArrayList<>();
}
