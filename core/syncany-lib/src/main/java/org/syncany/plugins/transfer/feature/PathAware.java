/*
 * Syncany, www.syncany.org
 * Copyright (C) 2011-2015 Philipp C. Heckel <philipp.heckel@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.syncany.plugins.transfer.feature;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.syncany.plugins.transfer.files.MultichunkRemoteFile;
import org.syncany.plugins.transfer.files.RemoteFile;
import org.syncany.plugins.transfer.files.TempRemoteFile;

/**
 * @author Christian Roth <christian.roth@port17.de>
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PathAware {
	int bytesPerFolder() default 2;
	int subfolderDepth() default 2;
	char folderSeparator() default '/';
	Class<? extends RemoteFile>[] affected() default {MultichunkRemoteFile.class, TempRemoteFile.class};
	Class<? extends TransferManagerFeatureExtension> extension();
}
