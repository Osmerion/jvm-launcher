/*
 * Copyright 2022-2025 Leon Linhart
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.osmerion.jvm.launcher.gradle

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/**
 * Represents a block of string file information.
 *
 * See [MSDN Docs](https://learn.microsoft.com/en-us/windows/win32/menurc/stringfileinfo-block)
 *
 * @since   0.1.0
 *
 * @author  Leon Linhart
 */
public interface StringFileInfoBlock {

    /**
     * Additional information that should be displayed for diagnostic purposes.
     *
     * @since   0.1.0
     */
    @get:Input
    @get:Optional
    public val comments: Property<String>

    /**
     * The name of the company that produced the file.
     *
     * This string is required.
     *
     * @since   0.1.0
     */
    @get:Input
    public val companyName: Property<String>

    /**
     * The description of the file.
     *
     * This string is required.
     *
     * @since   0.1.0
     */
    @get:Input
    public val fileDescription: Property<String>

    /**
     * A string representing the version of the file.
     *
     * @since   0.1.0
     */
    @get:Input
    public val fileVersion: Property<String>

    /**
     * The internal name of the file, if one exists.
     *
     * Defaults to the original name of the file.
     *
     * @since   0.1.0
     */
    @get:Input
    @get:Optional
    public val internalName: Property<String>

    /**
     * Copyright notices that apply to the file. This should include the full text of all notices, legal symbols,
     * copyright dates, and so on.
     *
     * @since   0.1.0
     */
    @get:Input
    @get:Optional
    public val legalCopyright: Property<String>

    /**
     * Trademarks and registered trademarks that apply to the file. This should include the full text of all notices,
     * legal symbols, trademark numbers, and so on.
     *
     * @since   0.1.0
     */
    @get:Input
    @get:Optional
    public val legalTrademarks: Property<String>

//    public val privateBuild: Property<String>

    /**
     * The name of the product with which the file is distributed.
     *
     * This string is required.
     *
     * @since   0.1.0
     */
    @get:Input
    public val productName: Property<String>

    /**
     * The version of the product with which the file is distributed.
     *
     * This string is required.
     *
     * @since   0.1.0
     */
    @get:Input
    public val productVersion: Property<String>

//    public val specialBuild: Property<String>

}