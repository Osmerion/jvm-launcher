/*
 * Guild Wars 2 Add-on Manager
 * Copyright (C) 2024-2025 Leon Linhart
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of version 3 of the GNU Lesser General Public License as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.osmerion.jvm.launcher.gradle

import org.gradle.api.Action
import org.gradle.api.Named
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property

/**
 * Represents a JVM launcher.
 *
 * @since   0.1.0
 *
 * @author  Leon Linhart
 */
public interface JvmLauncher : Named {

    // fixed-info

    /**
     * TODO doc
     *
     * @since   0.1.0
     */
    public val fileVersion: Property<VersionNumber>

    /**
     * TODO doc
     *
     * @since   0.1.0
     */
    public fun fileVersion(h1: Short, l1: Short, h2: Short, l2: Short)

    /**
     * TODO doc
     *
     * @since   0.1.0
     */
    public val productVersion: Property<VersionNumber>

    /**
     * TODO doc
     *
     * @since   0.1.0
     */
    public fun productVersion(h1: Short, l1: Short, h2: Short, l2: Short)

    // props

    /**
     * TODO doc
     *
     * @since   0.1.0
     */
    public fun stringFileInfo(action: Action<StringFileInfoBlock>)

    // other

    /**
     * A path to the icon for the executable.
     *
     * @since   0.1.0
     */
    public val icon: RegularFileProperty

}