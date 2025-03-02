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

/**
 * TODO doc
 *
 * @since   0.1.0
 *
 * @author  Leon Linhart
 */
public class VersionNumber(
    public val h1: Short,
    public val l1: Short,
    public val h2: Short,
    public val l2: Short
) {

    public operator fun component0(): Short = h1
    public operator fun component1(): Short = l1
    public operator fun component2(): Short = h2
    public operator fun component3(): Short = l2

    /**
     * TODO doc
     *
     * @since   0.1.0
     */
    public fun toString(separator: String): String = "$h1$separator$l1$separator$h2$separator$l2"

}