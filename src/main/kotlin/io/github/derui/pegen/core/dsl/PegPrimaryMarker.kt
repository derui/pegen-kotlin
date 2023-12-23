package io.github.derui.pegen.core.dsl

/**
 * A marker interface for syntax hierarchy.
 *
 * Hierarchy:
 * sequence -> prefix -> suffix -> primary
 */
sealed interface SyntaxHierarchyMarker

interface PegSequenceMarker : SyntaxHierarchyMarker

interface PegPrefixMarker : PegSequenceMarker

interface PegSuffixMarker : PegPrefixMarker

interface PegPrimaryMarker : PegSuffixMarker
