package dev.rushi.apkdownloadhelper

/*
 * Morphe Manager UI parity layer.
 *
 * Ported from morphe-manager's `ui/theme` and `ui/screen/shared` so the helper's screens
 * are built from the same tokens and components: exact light/dark color values, typography,
 * the shared metric set (radii, heights, touch targets, paddings) and the card / section /
 * info / selector components the manager uses everywhere.
 */

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Shared metrics, mirroring morphe-manager's `Defaults` in
 * `ui/screen/shared/SettingComponents.kt`.
 */
internal object MorpheDefaults {
    val CardElevation = 2.dp
    val CardCornerRadius = 16.dp
    val CompactCornerRadius = 12.dp
    val SettingsCornerRadius = 14.dp
    val SectionCornerRadius = 18.dp
    val IconSize = 24.dp
    val IconSizeSmall = 20.dp

    val MinTouchTarget = 48.dp
    val TallTouchTarget = 52.dp

    /** Compact pill holding an icon alone. */
    val PillHeight = 36.dp

    /** Pill that carries a label next to its icon. */
    val PillHeightLarge = 40.dp

    /** Fully rounded shape shared by the pill buttons. */
    val PillShape = RoundedCornerShape(50)

    /** Height of a glass tab or toggle. */
    val GlassButtonHeight = MinTouchTarget

    /** Height of a dialog action button. */
    val DialogButtonHeight = TallTouchTarget

    /** Width that keeps a compact action aligned with a wider icon + label sibling. */
    val CompactButtonWidth = 96.dp

    val ContentPaddingSmall = 8.dp
    val ContentPadding = 16.dp
    val ContentPaddingMedium = 24.dp
    val ContentPaddingExpanded = 32.dp
    val ItemSpacing = 12.dp

    val DefaultGradientColors = listOf(Color(0xFF1E5AA8), Color(0xFF00AFAE))

    const val ANIMATION_DURATION = 220
    const val ANIMATION_DURATION_SHORT = 180
    const val SCREEN_ENTER_DURATION = 320
    const val DIALOG_SCALE = 0.95f
}

/** Text colour inside Morphe-style dialogs (the manager resolves this per dialog surface). */
internal val LocalDialogTextColor = compositionLocalOf { Color.Unspecified }

// ---------------------------------------------------------------------------
// Colour scheme  exact values from morphe-manager's ui/theme/Color.kt
// ---------------------------------------------------------------------------

internal fun morpheDarkColorScheme() = darkColorScheme(
    primary = Color(0xFFA4C9FF),
    onPrimary = Color(0xFF00315D),
    primaryContainer = Color(0xFF004884),
    onPrimaryContainer = Color(0xFFD4E3FF),
    secondary = Color(0xFFBCC7DB),
    onSecondary = Color(0xFF263141),
    secondaryContainer = Color(0xFF3D4758),
    onSecondaryContainer = Color(0xFFD8E3F8),
    tertiary = Color(0xFFD9BDE3),
    onTertiary = Color(0xFF3D2946),
    tertiaryContainer = Color(0xFF543F5E),
    onTertiaryContainer = Color(0xFFF6D9FF),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF1A1C1E),
    onBackground = Color(0xFFE3E2E6),
    surface = Color(0xFF1A1C1E),
    onSurface = Color(0xFFE3E2E6),
    surfaceVariant = Color(0xFF43474E),
    onSurfaceVariant = Color(0xFFC3C6CF),
    outline = Color(0xFF8D9199),
    inverseOnSurface = Color(0xFF1A1C1E),
    inverseSurface = Color(0xFFE3E2E6),
    inversePrimary = Color(0xFF005FAC),
    surfaceTint = Color(0xFFA4C9FF),
    outlineVariant = Color(0xFF43474E),
    scrim = Color(0xFF000000),
)

internal fun morpheLightColorScheme() = lightColorScheme(
    primary = Color(0xFF005FAC),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD4E3FF),
    onPrimaryContainer = Color(0xFF001C39),
    secondary = Color(0xFF545F71),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD8E3F8),
    onSecondaryContainer = Color(0xFF111C2B),
    tertiary = Color(0xFF6D5677),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF6D9FF),
    onTertiaryContainer = Color(0xFF271430),
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onError = Color(0xFFFFFFFF),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFDFCFF),
    onBackground = Color(0xFF1A1C1E),
    surface = Color(0xFFFDFCFF),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFDFE2EB),
    onSurfaceVariant = Color(0xFF43474E),
    outline = Color(0xFF73777F),
    inverseOnSurface = Color(0xFFF1F0F4),
    inverseSurface = Color(0xFF2F3033),
    inversePrimary = Color(0xFFA4C9FF),
    surfaceTint = Color(0xFF005FAC),
    outlineVariant = Color(0xFFC3C6CF),
    scrim = Color(0xFF000000),
)

/** Manager typography: a single bodyLarge override, everything else M3 default. */
internal val MorpheTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

// ---------------------------------------------------------------------------
// Shared primitives
// ---------------------------------------------------------------------------

/** Scale-down-on-press modifier, as used by the manager's buttons and pills. */
@Composable
internal fun Modifier.pressScale(
    interactionSource: MutableInteractionSource,
    enabled: Boolean = true,
    pressedScale: Float = 0.96f
): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (enabled && pressed) pressedScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "press_scale"
    )
    return graphicsLayer { scaleX = scale; scaleY = scale }
}

/** Reusable icon with the manager's standard sizing and primary tint. */
@Composable
internal fun ThemedIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = MorpheDefaults.IconSize,
    tint: Color = MaterialTheme.colorScheme.primary,
    contentDescription: String? = null
) {
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier.size(size)
    )
}

/** Circle filled with the manager's brand gradient around an icon. */
@Composable
internal fun GradientCircleIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    iconSize: Dp = MorpheDefaults.IconSize,
    contentDescription: String? = null,
    gradientColors: List<Color> = MorpheDefaults.DefaultGradientColors
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(brush = Brush.linearGradient(gradientColors)),
        contentAlignment = Alignment.Center
    ) {
        ThemedIcon(
            icon = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            size = iconSize
        )
    }
}

/** Chevron pointing at whatever a row navigates to (mirrored for RTL). */
@Composable
internal fun ForwardChevronIcon(
    modifier: Modifier = Modifier,
    size: Dp = MorpheDefaults.IconSize,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    ThemedIcon(
        icon = if (androidx.compose.ui.platform.LocalConfiguration.current.layoutDirection ==
            android.util.LayoutDirection.RTL
        ) {
            Icons.Outlined.ChevronLeft
        } else {
            Icons.Outlined.ChevronRight
        },
        modifier = modifier,
        size = size,
        tint = tint
    )
}

/**
 * Base elevated card  the manager's `SurfaceCard`.
 */
@Composable
internal fun SurfaceCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    elevation: Dp = MorpheDefaults.CardElevation,
    cornerRadius: Dp = MorpheDefaults.CardCornerRadius,
    borderWidth: Dp = 0.dp,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    color: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(cornerRadius))
            .then(
                if (onClick != null) Modifier.clickable(enabled = enabled, onClick = onClick)
                else Modifier
            ),
        shape = RoundedCornerShape(cornerRadius),
        color = color,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = elevation,
        shadowElevation = 0.dp,
        border = if (borderWidth > 0.dp) BorderStroke(borderWidth, borderColor) else null
    ) {
        content()
    }
}

/** Section container card: roomier radius than a settings row, with a hairline border. */
@Composable
internal fun SectionCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    SurfaceCard(
        onClick = onClick,
        elevation = MorpheDefaults.CardElevation,
        cornerRadius = MorpheDefaults.SectionCornerRadius,
        borderWidth = 1.dp,
        modifier = modifier
    ) {
        content()
    }
}

/** Grouped-settings container. */
@Composable
internal fun SettingsGroup(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    SectionCard(modifier = modifier) {
        Column(content = content)
    }
}

/** Settings item card: tighter radius and lighter elevation than a section card. */
@Composable
internal fun SettingsItemCard(
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    borderWidth: Dp = 0.dp,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    color: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
    content: @Composable () -> Unit
) {
    SurfaceCard(
        onClick = onClick,
        enabled = enabled,
        elevation = 1.dp,
        cornerRadius = MorpheDefaults.SettingsCornerRadius,
        borderWidth = borderWidth,
        borderColor = borderColor,
        color = color,
        modifier = modifier
    ) {
        content()
    }
}

/** Horizontal divider tinted the way the manager tints its settings dividers. */
@Composable
internal fun MorpheDivider(
    modifier: Modifier = Modifier,
    fullWidth: Boolean = false
) {
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant
    val surfaceTint = MaterialTheme.colorScheme.surfaceTint
    val color = remember(outlineVariant, surfaceTint) {
        lerp(outlineVariant, surfaceTint, 0.18f).copy(alpha = 0.55f)
    }
    HorizontalDivider(
        modifier = if (fullWidth) modifier else modifier.padding(horizontal = MorpheDefaults.ContentPadding),
        color = color
    )
}

/** Row of optional leading content, title/description column and optional trailing content. */
@Composable
internal fun IconTextRow(
    modifier: Modifier = Modifier,
    leadingContent: @Composable (() -> Unit)? = null,
    title: String,
    description: String? = null,
    titleStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    titleWeight: FontWeight = FontWeight.Medium,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    descriptionStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    descriptionColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    trailingContent: @Composable (() -> Unit)? = null,
    spacing: Dp = MorpheDefaults.ItemSpacing
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingContent?.invoke()

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = titleStyle,
                fontWeight = titleWeight,
                color = titleColor
            )
            description?.let {
                Text(
                    text = it,
                    style = descriptionStyle,
                    color = descriptionColor
                )
            }
        }

        trailingContent?.invoke()
    }
}

/** Standard settings row with icon + chevron. */
@Composable
internal fun SettingsItem(
    onClick: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    subtitle: String? = null,
    showBorder: Boolean = false,
    statusContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = { ForwardChevronIcon() }
) {
    SettingsItemCard(
        onClick = onClick,
        borderWidth = if (showBorder) 1.dp else 0.dp,
        modifier = modifier
    ) {
        IconTextRow(
            modifier = Modifier.padding(MorpheDefaults.ContentPadding),
            leadingContent = leadingContent ?: icon?.let { { ThemedIcon(icon = it) } },
            title = title,
            description = subtitle,
            trailingContent = when (statusContent) {
                null -> trailingContent
                else -> {
                    {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(MorpheDefaults.ContentPaddingSmall),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            statusContent()
                            trailingContent?.invoke()
                        }
                    }
                }
            }
        )
    }
}

/** Section title with the manager's gradient circle icon. */
@Composable
internal fun MorpheSectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(MorpheDefaults.ItemSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            GradientCircleIcon(icon = icon, size = 36.dp, iconSize = 20.dp)
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/** Card header: tinted, top-rounded strip above a card's content. */
@Composable
internal fun MorpheCardHeader(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    description: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(
                topStart = MorpheDefaults.SectionCornerRadius,
                topEnd = MorpheDefaults.SectionCornerRadius
            )
        ) {
            IconTextRow(
                modifier = Modifier.padding(MorpheDefaults.ContentPadding),
                leadingContent = { ThemedIcon(icon = icon) },
                title = title,
                description = description
            )
        }
    }
}

/** Expandable surface with a header icon, title and collapsible content. */
@Composable
internal fun MorpheExpandableSurface(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    initialExpanded: Boolean = false,
    headerTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(initialExpanded) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(MorpheDefaults.ANIMATION_DURATION),
        label = "morphe_expand_rotation"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MorpheDefaults.CompactCornerRadius)),
        shape = RoundedCornerShape(MorpheDefaults.CompactCornerRadius),
        color = headerTint.copy(alpha = 0.05f)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (icon != null) {
                        ThemedIcon(
                            icon = icon,
                            size = MorpheDefaults.IconSizeSmall,
                            tint = headerTint
                        )
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = headerTint
                    )
                }

                ThemedIcon(
                    icon = Icons.Outlined.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    modifier = Modifier.rotate(rotationAngle),
                    size = MorpheDefaults.IconSizeSmall,
                    tint = headerTint.copy(alpha = 0.7f)
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(tween(MorpheDefaults.ANIMATION_DURATION)) + fadeIn(),
                exit = shrinkVertically(tween(MorpheDefaults.ANIMATION_DURATION)) + fadeOut()
            ) {
                content()
            }
        }
    }
}

/** Switch with check/close icons in the thumb, as the manager draws them. */
@Composable
internal fun MorpheToggleSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = SwitchDefaults.colors(checkedIconColor = MaterialTheme.colorScheme.primary),
        thumbContent = {
            Icon(
                imageVector = if (checked) Icons.Filled.Check else Icons.Filled.Close,
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize)
            )
        }
    )
}

/** One card of a [MorpheSelectorRow]. */
internal data class MorpheSelectorOption(
    val label: String,
    val icon: ImageVector
)

/**
 * Row of equally wide cards for switching between a few modes  the manager's
 * `CardSelectorRow`. The selected mode is carried by the fill and a stronger border.
 */
@Composable
internal fun MorpheSelectorRow(
    options: List<MorpheSelectorOption>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        options.forEachIndexed { index, option ->
            val isSelected = index == selectedIndex
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .selectable(
                        selected = isSelected,
                        role = Role.Tab,
                        onClick = { onSelect(index) }
                    ),
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    Color.Transparent
                },
                border = BorderStroke(
                    width = if (isSelected) 1.5.dp else 0.5.dp,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    }
                )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemedIcon(
                        icon = option.icon,
                        tint = if (isSelected) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        }
                    )
                    Text(
                        text = option.label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Pill-shaped action button with an icon and optional label  the manager's `ActionPillButton`,
 * shared by the compact card actions.
 */
@Composable
internal fun MorphePillButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    enabled: Boolean = true,
    tall: Boolean = false,
    tone: SemanticTone = SemanticTone.Neutral
) {
    val interactionSource = remember { MutableInteractionSource() }
    val height = if (tall) MorpheDefaults.PillHeightLarge else MorpheDefaults.PillHeight
    val iconSize = if (tall) 20.dp else 18.dp

    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = MorpheDefaults.PillShape,
        color = if (enabled) tone.container else tone.container.copy(alpha = 0.4f),
        contentColor = if (enabled) tone.content else tone.content.copy(alpha = 0.5f),
        interactionSource = interactionSource,
        modifier = modifier
            .height(height)
            .pressScale(interactionSource = interactionSource, enabled = enabled)
            .semantics { role = Role.Button }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MorpheDefaults.ContentPadding),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(iconSize)
                )
                label?.let {
                    Text(
                        text = it,
                        style = if (tall) {
                            MaterialTheme.typography.labelLarge
                        } else {
                            MaterialTheme.typography.labelSmall
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/** Centred stat box: a bold value over an optional caption. */
@Composable
internal fun InfoStatBox(
    value: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MorpheDefaults.CompactCornerRadius),
        color = containerColor
    ) {
        Column(
            modifier = Modifier.padding(MorpheDefaults.ContentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = valueColor.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/** Hero header used at the top of prominent cards: circular icon, title, optional subtitle. */
@Composable
internal fun HeroInfoCard(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
    iconContainerColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
    iconTint: Color = MaterialTheme.colorScheme.primary,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    footer: (@Composable ColumnScope.() -> Unit)? = null,
    subtitle: (@Composable RowScope.() -> Unit)? = null
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MorpheDefaults.SectionCornerRadius),
        color = containerColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MorpheDefaults.ContentPadding),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = iconContainerColor,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = titleColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (subtitle != null) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            content = subtitle
                        )
                    }
                }
            }

            footer?.invoke(this)
        }
    }
}

/** Grouped information container with a title and an optional trailing icon. */
@Composable
internal fun InfoBox(
    title: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    icon: ImageVector? = null,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MorpheDefaults.CompactCornerRadius),
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = titleColor
                )

                content()
            }

            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = iconTint
                )
            }
        }
    }
}

/** Centred empty state with an oversized icon and optional action. */
@Composable
internal fun MorpheEmptyState(
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = Icons.Outlined.FolderOff,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (actionLabel != null && onAction != null) {
            OutlinedButton(onClick = onAction) {
                Text(actionLabel)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Semantic tones and status badges  ports of the manager's `StatusBadge.kt`
// ---------------------------------------------------------------------------

/**
 * Semantic colour roles shared by everything that carries a tint: badges, notices and status
 * rows. One definition, so the same meaning cannot read as two different colours in two
 * screens.
 */
internal enum class SemanticTone {
    Neutral,
    Primary,
    Success,
    Warning,
    Error;

    /** Background of a filled element in this role. */
    val container: Color
        @Composable get() = when (this) {
            Neutral -> MaterialTheme.colorScheme.surfaceVariant
            Primary -> MaterialTheme.colorScheme.primaryContainer
            Success -> MaterialTheme.colorScheme.tertiaryContainer
            Warning -> MaterialTheme.colorScheme.secondaryContainer
            Error -> MaterialTheme.colorScheme.errorContainer
        }

    /** Content drawn on top of [container]. */
    val content: Color
        @Composable get() = when (this) {
            Neutral -> MaterialTheme.colorScheme.onSurfaceVariant
            Primary -> MaterialTheme.colorScheme.onPrimaryContainer
            Success -> MaterialTheme.colorScheme.onTertiaryContainer
            Warning -> MaterialTheme.colorScheme.onSecondaryContainer
            Error -> MaterialTheme.colorScheme.onErrorContainer
        }

    /** Standalone colour for text or icons carrying the role without a filled background. */
    val accent: Color
        @Composable get() = when (this) {
            Neutral -> MaterialTheme.colorScheme.onSurfaceVariant
            Primary -> MaterialTheme.colorScheme.primary
            Success -> MaterialTheme.colorScheme.tertiary
            Warning -> MaterialTheme.colorScheme.secondary
            Error -> MaterialTheme.colorScheme.error
        }
}

/** Sizing shared by every badge, so badges line up wherever they end up side by side. */
private object BadgeDefaults {
    val HorizontalPadding = 10.dp
    val VerticalPadding = 4.dp
    val IconSize = 14.dp
    val ItemSpacing = 5.dp
}

/**
 * Filter chip for the lists that narrow down  the manager's `AppFilterChip`. Carries a fill of
 * its own rather than the platform's transparent one, which would show the raised surface back
 * and leave only a hairline to say a button is there.
 */
@Composable
internal fun MorpheFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    selectedIcon: ImageVector = Icons.Outlined.Done
) {
    val scheme = MaterialTheme.colorScheme

    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        modifier = modifier,
        leadingIcon = if (selected) {
            { Icon(selectedIcon, contentDescription = null, modifier = Modifier.size(16.dp)) }
        } else {
            null
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = scheme.surfaceColorAtElevation(2.dp),
            labelColor = scheme.onSurfaceVariant,
            selectedContainerColor = scheme.primaryContainer,
            selectedLabelColor = scheme.onPrimaryContainer,
            selectedLeadingIconColor = scheme.onPrimaryContainer
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = scheme.outline.copy(alpha = 0.5f),
            selectedBorderColor = scheme.primary,
            selectedBorderWidth = 1.dp
        )
    )
}

/**
 * Inline status marker, sized to its content.
 *
 * @param text Badge label, or null for a badge that is only its [icon]  dropping the label is
 *   for markers sharing a row with badges that need the room for their own words.
 * @param tone Semantic colour role
 * @param containerColor Background override, for badges drawn over custom artwork
 * @param contentColor Content override, paired with [containerColor]
 * @param onClick Makes the badge act as a control
 */
@Composable
internal fun MorpheStatusBadge(
    text: String?,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    tone: SemanticTone = SemanticTone.Neutral,
    containerColor: Color = tone.container,
    contentColor: Color = tone.content,
    onClick: (() -> Unit)? = null
) {
    // Zero-width spaces so long tokens break at "/" and "." instead of overflowing the pill.
    val breakableText = remember(text) {
        text?.replace("/", "/\u200B")?.replace(".", ".\u200B")
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(containerColor)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(
                horizontal = BadgeDefaults.HorizontalPadding,
                vertical = BadgeDefaults.VerticalPadding
            ),
        horizontalArrangement = Arrangement.spacedBy(BadgeDefaults.ItemSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            ThemedIcon(icon = it, tint = contentColor, size = BadgeDefaults.IconSize)
        }
        breakableText?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Badges on a line of their own, wrapping onto the next one when they run out of room.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun MorpheStatusBadgeRow(
    modifier: Modifier = Modifier,
    content: @Composable FlowRowScope.() -> Unit
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(BadgeDefaults.ItemSpacing),
        verticalArrangement = Arrangement.spacedBy(BadgeDefaults.ItemSpacing),
        content = content
    )
}

/**
 * Semi-transparent dialog action button (the manager's `AppDialogButton` family), shared by
 * every confirmation surface in the helper.
 */
@Composable
internal fun MorpheDialogButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    isDestructive: Boolean = false,
    filled: Boolean = true,
    textSuffix: String? = null
) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val isDark = !textColor.isDarkColor()
    val primaryColor = MaterialTheme.colorScheme.primary
    val destructiveDark = Color(0xFFFF6B6B)
    val destructiveLight = Color(0xFFD32F2F)

    val containerColor = when {
        isDestructive && filled -> Color.Red.copy(alpha = if (isDark) 0.25f else 0.2f)
        isDestructive -> Color.Transparent
        filled -> primaryColor.copy(alpha = if (isDark) 0.3f else 0.25f)
        else -> Color.Transparent
    }
    val contentColor = when {
        isDestructive -> if (isDark) destructiveDark else destructiveLight
        filled -> textColor
        else -> textColor.copy(alpha = 0.85f)
    }
    val borderColor = when {
        isDestructive -> Color.Red.copy(alpha = if (isDark) 0.4f else 0.35f)
        filled -> primaryColor.copy(alpha = if (isDark) 0.5f else 0.4f)
        else -> primaryColor.copy(alpha = if (isDark) 0.3f else 0.25f)
    }

    val interactionSource = remember { MutableInteractionSource() }
    val buttonModifier = modifier
        .height(MorpheDefaults.DialogButtonHeight)
        .pressScale(interactionSource = interactionSource, enabled = enabled)
    val shape = RoundedCornerShape(MorpheDefaults.CardCornerRadius)
    val border = BorderStroke(1.dp, borderColor)
    val contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
    val content: @Composable RowScope.() -> Unit = {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(MorpheDefaults.IconSizeSmall)
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = if (textSuffix == null) TextOverflow.Ellipsis else TextOverflow.Clip
        )
        if (textSuffix != null) {
            Spacer(Modifier.width(4.dp))
            Text(
                text = textSuffix,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                modifier = Modifier.weight(1f, fill = false)
            )
        }
    }

    if (filled) {
        Button(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled,
            interactionSource = interactionSource,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor,
                disabledContainerColor = containerColor.copy(alpha = 0.5f),
                disabledContentColor = contentColor.copy(alpha = 0.5f)
            ),
            border = border,
            contentPadding = contentPadding,
            content = content
        )
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled,
            interactionSource = interactionSource,
            shape = shape,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = contentColor,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = contentColor.copy(alpha = 0.4f)
            ),
            border = border,
            contentPadding = contentPadding,
            content = content
        )
    }
}

/** Approximate luminance test for picking destructive accents. */
private fun Color.isDarkColor(): Boolean =
    (0.299 * red + 0.587 * green + 0.114 * blue) < 0.5

/** Dialog content padding matching the manager's dialog chrome. */
internal val MorpheDialogContentPadding = PaddingValues(
    horizontal = MorpheDefaults.ContentPaddingMedium,
    vertical = MorpheDefaults.ContentPadding
)

/**
 * Shared dialog properties: dismiss on back/outside, sized by the caller's own surface.
 */
internal val MorpheDialogProperties = DialogProperties(usePlatformDefaultWidth = false)

/**
 * Dialog chrome shared by the helper's dialogs: manager surface, title, content and optional
 * actions, so call sites stop hand-assembling their own dialog layout.
 */
@Composable
internal fun MorpheDialog(
    title: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    actions: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(onDismissRequest = onDismiss, properties = MorpheDialogProperties) {
        MorpheDialogSurface(modifier = modifier.fillMaxWidth(0.92f)) {
            Column(
                modifier = Modifier.padding(MorpheDialogContentPadding),
                verticalArrangement = Arrangement.spacedBy(MorpheDefaults.ContentPadding)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = LocalDialogTextColor.current
                )
                content()
                actions?.let { actions ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MorpheDefaults.ItemSpacing),
                        verticalAlignment = Alignment.CenterVertically,
                        content = actions
                    )
                }
            }
        }
    }
}

/** Wraps dialog content so nested rows resolve their text colour from the surface. */
@Composable
internal fun MorpheDialogSurface(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MorpheDefaults.SectionCornerRadius),
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
    ) {
        CompositionLocalProvider(
            LocalDialogTextColor provides MaterialTheme.colorScheme.onSurface
        ) {
            Column(content = content)
        }
    }
}
