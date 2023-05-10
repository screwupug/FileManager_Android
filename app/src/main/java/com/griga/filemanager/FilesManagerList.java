package com.griga.filemanager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


public class FilesManagerList extends Fragment {
   private  List<File> filesList;
   private List<File> dirsList;
   private List<File> resultList;
   private CustomAdapter adapter;
   private RecyclerView recyclerView;
   private File root;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_files_manager_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerViewInit();
        return view;
    }

    private void recyclerViewInit() {
        root = new File(getPath());
        getResultListSortedByName();
        adapter = new CustomAdapter(resultList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.size_ascending) {
            Toast.makeText(getContext(), "Сортировка по возрастанию", Toast.LENGTH_SHORT).show();
            ascendingSortBySize();
            return true;
        }
        if (item.getItemId() == R.id.size_descending) {
            Toast.makeText(getContext(), "Сортировка по убыванию", Toast.LENGTH_SHORT).show();
            descendingSortBySize();
            return true;
        }
        if (item.getItemId() == R.id.extension_ascending) {
            ascendingSortByExtension();
            Toast.makeText(getContext(), "Сортировка по возрастанию", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (item.getItemId() == R.id.extension_descending) {
            Toast.makeText(getContext(), "Сортировка по убыванию", Toast.LENGTH_SHORT).show();
            descendingSortByExtension();
            return true;
        }
        if (item.getItemId() == R.id.date_ascending) {
            Toast.makeText(getContext(), "Сортировка по возрастанию", Toast.LENGTH_SHORT).show();
            ascendingSortByDate();
            return true;
        }
        if (item.getItemId() == R.id.date_descending) {
            Toast.makeText(getContext(), "Сортировка по убыванию", Toast.LENGTH_SHORT).show();
            descendingSortByDate();
            return true;
        }
        if (item.getItemId() == R.id.sort_by_name) {
            Toast.makeText(getContext(), "Сортировка по-умолчанию", Toast.LENGTH_SHORT).show();
            ascendingSortByName();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getPath() {
        if (getArguments() == null) {
            return Environment.getExternalStorageDirectory().getPath();
        } else {
            return getArguments().getString("path");
        }
    }

    private List<File> getDirsList() {
        dirsList = new ArrayList<>();
        for (File file : Objects.requireNonNull(root.listFiles())) {
            if (file.isDirectory()) {
                dirsList.add(file);
            }
        }
        return dirsList;
    }

    private List<File> getFilesList() {
        filesList = new ArrayList<>();
        for (File file : Objects.requireNonNull(root.listFiles())) {
            if (!file.isDirectory()) {
                filesList.add(file);
            }
        }
        return filesList;
    }

    private void sortDirsByName() {
        dirsList = getDirsList();
        dirsList.sort(Comparator.comparing(file -> file.getName().toLowerCase()));
    }

    private void sortFilesByName() {
        filesList = getFilesList();
        filesList.sort(Comparator.comparing(file -> file.getName().toLowerCase()));
    }

    private void getResultListSortedByName() {
        sortDirsByName();
        sortFilesByName();
        resultList = new ArrayList<>();
        resultList.addAll(dirsList);
        resultList.addAll(filesList);
    }

    private void ascendingSortByName() {
        getResultListSortedByName();
        adapter.setNewList(resultList);
    }

    private void ascendingSortBySize() {
        resultList = new ArrayList<>();
        filesList.sort(Comparator.comparing(File::length));
        resultList.addAll(dirsList);
        resultList.addAll(filesList);
        adapter.setNewList(resultList);
    }

    private void descendingSortBySize() {
        resultList = new ArrayList<>();
        filesList.sort(Comparator.comparing(File::length).reversed());
        resultList.addAll(dirsList);
        resultList.addAll(filesList);
        adapter.setNewList(resultList);
    }

    private void ascendingSortByExtension() {
        resultList = new ArrayList<>();
        filesList.sort(Comparator.comparing(file -> FilenameUtils.getExtension(file.toString())));
        resultList.addAll(dirsList);
        resultList.addAll(filesList);
        adapter.setNewList(resultList);
    }

    private void descendingSortByExtension() {
        resultList = new ArrayList<>();
        filesList.sort(Comparator.comparing(file -> FilenameUtils.getExtension(file.toString())).reversed());
        resultList.addAll(dirsList);
        resultList.addAll(filesList);
        adapter.setNewList(resultList);
    }

    private void ascendingSortByDate() {
        resultList = new ArrayList<>();
        filesList.sort(Comparator.comparing(File::lastModified));
        resultList.addAll(dirsList);
        resultList.addAll(filesList);
        adapter.setNewList(resultList);
    }

    private void descendingSortByDate() {
        resultList = new ArrayList<>();
        filesList.sort(Comparator.comparing(File::lastModified).reversed());
        resultList.addAll(dirsList);
        resultList.addAll(filesList);
        adapter.setNewList(resultList);
    }
}