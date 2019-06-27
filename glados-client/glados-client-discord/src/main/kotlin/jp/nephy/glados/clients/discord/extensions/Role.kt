/*
 * The MIT License (MIT)
 *
 *     Copyright (c) 2017-2019 Nephy Project Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

@file:Suppress("UNUSED")

package jp.nephy.glados.clients.discord.extensions

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction
import net.dv8tion.jda.api.requests.restaction.RoleAction
import java.awt.Color

/**
 * Checks if this member has the role.
 */
fun Member.hasRole(id: Long): Boolean {
    return roles.any { it.idLong == id }
}

/**
 * Checks if this member has the role.
 */
fun Member.hasRole(role: Role): Boolean {
    return role in roles
}

/**
 * Adds the role to this member.
 */
fun Member.addRole(id: Long): AuditableRestAction<Void>? {
    return addRole(jda.getRoleById(id) ?: return null)
}

/**
 * Adds the role to this member.
 */
fun Member.addRole(role: Role): AuditableRestAction<Void>? {
    if (hasRole(role)) {
        return null
    }
    
    return guild.addRoleToMember(this, role)
}

/**
 * Removes the role from this member.
 */
fun Member.removeRole(id: Long): AuditableRestAction<Void>? {
    return removeRole(jda.getRoleById(id) ?: return null)
}

/**
 * Removes the role from this member.
 */
fun Member.removeRole(role: Role): AuditableRestAction<Void>? {
    if (!hasRole(role)) {
        return null
    }
    
    return guild.addRoleToMember(this, role)
}

fun Guild.createRole(name: String? = null, color: Color? = null, mentionable: Boolean? = null, hoisted: Boolean? = null, permissions: List<Permission>): RoleAction {
    return createRole().setName(name).setColor(color).setMentionable(mentionable).setHoisted(hoisted).setPermissions(permissions)
}
