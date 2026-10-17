package com.example.android.architecture.blueprints.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class TodoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .width(250.dp)
                            .height(80.dp)
                    ) {
                        Text("BLEU")
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {},
                        modifier = Modifier
                            .width(250.dp)
                            .height(80.dp)
                    ) {
                        Text("ROUGE")
                    }
                }
            }
        }
    }
}
