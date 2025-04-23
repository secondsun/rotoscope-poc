//            MenuItem(
//                onClick = {
//                    // Show file save dialog
//                    scope.launch {
//                        // If we have a saved file path, use it, otherwise show file chooser
//                        val currentFilePath = appModel.project.value.filePath
//
//                        if (currentFilePath.isNotEmpty()) {
//                            // Save directly to the existing file
//                            val success = appModel.saveProject(File(currentFilePath))
//                            if (success) {
//                                //TODO Show success message
//                            } else {
//                                //TODO Show error message
//                            }
//                        } else {
//                            // No existing path, show file chooser
//                            showSaveFileChooser(appModel, scope)
//
//                            // Set initial directory to the current file's directory if it exists
//                            val currentFilePath = appModel.project.value.filePath
//                            if (currentFilePath.isNotEmpty()) {
//                                val currentFile = File(currentFilePath)
//                                if (currentFile.parentFile?.exists() == true) {
//                                    currentDirectory = currentFile.parentFile
//                                }
//                            }
//                        }
//                    }
//                },
//                text = "Save Project"
//            )
//
//            MenuItem(
//                onClick = {
//                    // Always show file save dialog for "Save As"
//                    scope.launch {
//                        showSaveFileChooser(appModel, scope)
//                    }
//                },
//                text = "Save Project As..."
//            )
