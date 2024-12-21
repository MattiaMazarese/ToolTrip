package menù;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import login.ProfileActivity;

import group.MyGroupActivity;
import item.VisualizzaToolActivity;

public class MenuHandler {

    private Context context;

    public MenuHandler(Context context) {
        this.context = context;
    }

    public void setUpMenuListeners(View homeIcon, View addToolIcon, View viewToolIcon, View profileIcon) {
        // Listener per l'icona Home
        homeIcon.setOnClickListener(v -> {
            context.startActivity(new Intent(context, HomeActivity.class));
        });

        // Listener per l'icona AggiungiTool
        addToolIcon.setOnClickListener(v -> {
            context.startActivity(new Intent(context, VisualizzaToolActivity.class));
        });

        // Listener per l'icona VisualizzaTool
        viewToolIcon.setOnClickListener(v -> {
            context.startActivity(new Intent(context, MyGroupActivity.class));
        });

        // Listener per l'icona Profilo
        profileIcon.setOnClickListener(v -> {
            context.startActivity(new Intent(context, ProfileActivity.class));
        });
    }
}
