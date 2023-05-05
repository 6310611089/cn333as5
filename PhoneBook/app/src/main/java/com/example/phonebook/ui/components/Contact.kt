package com.example.phonebook.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.phonebook.domain.model.PhoneBookModel
import com.example.phonebook.util.fromHex

@ExperimentalMaterialApi
@Composable
fun Contact(
    modifier: Modifier = Modifier,
    contact: PhoneBookModel,
    onContactClick: (PhoneBookModel) -> Unit = {},
    isSelected: Boolean
) {
    val background = if (isSelected)
        Color.LightGray
    else
        MaterialTheme.colors.surface

    Card(
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        backgroundColor = background
    ) {
        ListItem(
            text = { Text(text = contact.name, maxLines = 1) },
            secondaryText = {
                Text(text = contact.phone_number, maxLines = 1)
            },
            icon = {
                ContactColor(
                    color = Color.fromHex(contact.color.hex),
                    size = 40.dp,
                    border = 1.dp
                )
            },
            modifier = Modifier.clickable {
                onContactClick.invoke(contact)
            }
        )
    }
}

@ExperimentalMaterialApi
@Preview
@Composable
private fun ContactPreview() {
    Contact(contact = PhoneBookModel(1, "Contact 1", "0812345678"), isSelected = true)
}