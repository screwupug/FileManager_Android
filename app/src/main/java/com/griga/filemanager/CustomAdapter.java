package com.griga.filemanager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private List<File> filesList;
    private final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy.MM.dd  HH:mm", Locale.getDefault());
    private final Context CONTEXT;

    public CustomAdapter(List<File> files, Context context) {
        this.filesList = files;
        this.CONTEXT = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.files_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        File selectedFile = filesList.get(position);
        Date date = new Date(selectedFile.lastModified());
        String extension = FilenameUtils.getExtension(selectedFile.toString());
        holder.date.setText(FORMATTER.format(date));
        holder.name.setText(selectedFile.getName());

        if (selectedFile.isDirectory()) {
            holder.icon.setImageResource(R.drawable.baseline_folder_24);
            holder.size.setText("<dir>");
        } else {
            holder.size.setText(Formatter.formatFileSize(CONTEXT, selectedFile.length()));
            switch (extension) {
                case "jpeg":
                case "png":
                case "svg":
                case "jpg":
                    holder.icon.setImageResource(R.drawable.image);
                    break;
                case "pdf":
                    holder.icon.setImageResource(R.drawable.pdf);
                    break;
                case "rar":
                case "zip":
                    holder.icon.setImageResource(R.drawable.folder_zip);
                    break;
                case "apk":
                    holder.icon.setImageResource(R.drawable.apk_install);
                    break;
                case "txt":
                case "doc":
                case "xml":
                case "xlsx":
                case "pptx":
                    holder.icon.setImageResource(R.drawable.baseline_description_24);
                    break;
                default:
                    holder.icon.setImageResource(R.drawable.unknown_document);
                    break;
            }
        }

        holder.itemView.setOnClickListener(view -> {
            if (selectedFile.isDirectory()) {
                if (selectedFile.getName().equals("Android")) {
                    Toast.makeText(CONTEXT, "Доступ запрещен", Toast.LENGTH_SHORT).show();
                } else {
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    FilesManagerList filesManagerList = new FilesManagerList();
                    Bundle bundle = new Bundle();
                    bundle.putString("path", selectedFile.getAbsolutePath());
                    filesManagerList.setArguments(bundle);
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.activity_main, filesManagerList)
                            .commit();

                    // полная шляпа
                    activity.getSupportActionBar().setTitle(selectedFile.getName());
                }
            } else {
                Uri fileUri = FileProvider.getUriForFile(CONTEXT,
                        CONTEXT.getApplicationContext().getPackageName() + ".provider",
                        selectedFile);
                String mime = CONTEXT.getContentResolver().getType(fileUri);
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(fileUri, mime);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    CONTEXT.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    showNoCompatibleAppMessage();
                }
            }
        });

        holder.itemView.setOnLongClickListener(view -> {
            if (!selectedFile.isDirectory()) {
                Uri fileUri = FileProvider.getUriForFile(CONTEXT,
                        CONTEXT.getApplicationContext().getPackageName() + ".provider",
                        selectedFile);
                String mime = CONTEXT.getContentResolver().getType(fileUri);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType(mime);
                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                CONTEXT.startActivity(Intent.createChooser(shareIntent, null));
                return true;
            }
            return false;
        });

    }

    private void showNoCompatibleAppMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CONTEXT);
        AlertDialog dialog = builder.create();
        dialog.setTitle(R.string.alert_dialog_no_compatible_app);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", (dialogInterface, i) -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return filesList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setNewList(List<File> list) {
        filesList = list;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView size;
        TextView date;
        ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            name = itemView.findViewById(R.id.name);
            size = itemView.findViewById(R.id.size);
            date = itemView.findViewById(R.id.date);
        }
    }
}
