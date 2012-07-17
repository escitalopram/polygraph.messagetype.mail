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

import lombok.Data;

@Data
public class HeaderEdit implements Serializable {

	private static final long serialVersionUID = -3642055321229952238L;

	public static enum Operation {
		add,replace,remove
	}
	private final String name;
	private final String content;
	private final Operation operation;
}
