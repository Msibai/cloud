package com.msibai.cloud.helpers;

import com.msibai.cloud.entities.Folder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestHelper {
    public List<Folder> createListOfFoldersWithUserId(List<String> folderNames, UUID userId) {
        List<Folder> folders = new ArrayList<>();
        for (String name : folderNames) {
            folders.add(createFolderWithNameAndUserId(name, userId));
        }
        return folders;
    }

    public Folder createFolderWithNameAndUserId(String folderName, UUID userId) {
        Folder folder = new Folder();
        folder.setFolderName(folderName);
        folder.setUserId(userId);
        return folder;
    }
}
