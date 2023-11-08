package io.agora.chatroom.compose.chatbottombar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import io.agora.chatroom.commons.ComposerInputMessageState
import io.agora.chatroom.viewmodel.messages.MessageChatBarViewModel
import io.agora.chatroom.widget.WidgetInputField

/**
 * Input field for the Messages/Conversation screen. Allows label customization, as well as handlers
 * when the input changes.
 *
 * @param composerMessageState The state of the input.
 * @param onValueChange Handler when the value changes.
 * @param modifier Modifier for styling.
 * @param maxLines The number of lines that are allowed in the input.
 * @param keyboardOptions The [KeyboardOptions] to be applied to the input.
 * @param label Composable that represents the label UI, when there's no input.
 * @param innerLeadingContent Composable that represents the persistent inner leading content.
 * @param innerTrailingContent Composable that represents the persistent inner trailing content.
 */
@Composable
fun ComposeMessageInput(
    composerMessageState: ComposerInputMessageState,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MessageChatBarViewModel,
    maxLines: Int = DefaultMessageInputMaxLines,
    keyboardOptions: KeyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
    label: @Composable (ComposerInputMessageState) -> Unit = {
        DefaultComposerLabel(ownCapabilities = composerMessageState.ownCapabilities)
    },
    innerLeadingContent: @Composable RowScope.() -> Unit = {},
    innerTrailingContent: @Composable RowScope.() -> Unit = {},
) {
    val (value) = composerMessageState

    WidgetInputField(
        modifier = modifier,
        value = value,
        maxLines = maxLines,
        onValueChange = onValueChange,
        enabled = true,
        viewModel = viewModel,
        innerPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        keyboardOptions = keyboardOptions,
        decorationBox = { innerTextField ->
            Column {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    innerLeadingContent()

                    Box(modifier = Modifier.weight(1f)) {
                        innerTextField()

                        if (value.isEmpty()) {
                            label(composerMessageState)
                        }
                    }

                    innerTrailingContent()
                }
            }
        }
    )
}

/**
 * The default number of lines allowed in the input. The message input will become scrollable after
 * this threshold is exceeded.
 */
private const val DefaultMessageInputMaxLines = 6
