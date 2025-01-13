package menù;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;

import login.ProfileActivity;
import group.MyGroupActivity;
import item.VisualizzaToolActivity;
import menù.HomeActivity;

import com.example.tooltrip.R;

public class MenuHandler {



    private Context context;
    private ImageView homeIcon, addToolIcon, viewToolIcon, profileIcon;

    // Costante per il nome delle SharedPreferences
    private static final String PREFS_NAME = "MenuPrefs";
    private static final String PREF_SELECTED_ICON = "selectedIcon";

    // Costante per le icone
    private static final String HOME_SELECTED = "home";
    private static final String ADD_TOOL_SELECTED = "addTool";
    private static final String VIEW_TOOL_SELECTED = "viewTool";
    private static final String PROFILE_SELECTED = "profile";

    public MenuHandler(Context context) {
        this.context = context;
    }

    private String activeSection;

    public MenuHandler(Context context, String activeSection) {
        this.context = context;
        this.activeSection = activeSection;
    }




    public void setUpMenuListeners(View homeIcon, View addToolIcon, View viewToolIcon, View profileIcon) {
        this.homeIcon = (ImageView) homeIcon;
        this.addToolIcon = (ImageView) addToolIcon;
        this.viewToolIcon = (ImageView) viewToolIcon;
        this.profileIcon = (ImageView) profileIcon;

        // Recupera lo stato di selezione salvato
        String selectedIcon = getSelectedIconFromPrefs();

        if(activeSection == "viewTool"){
            selectedIcon = VIEW_TOOL_SELECTED;
        }else {
            selectedIcon = getSelectedIconFromPrefs();
        }

        // Imposta l'icona selezionata in base al valore recuperato
        if (selectedIcon != null) {
            switch (selectedIcon) {
                case HOME_SELECTED:
                    updateSelectedIcon((ImageView) homeIcon);
                    break;
                case ADD_TOOL_SELECTED:
                    updateSelectedIcon((ImageView) addToolIcon);
                    break;
                case VIEW_TOOL_SELECTED:
                    updateSelectedIcon((ImageView) viewToolIcon);
                    break;
                case PROFILE_SELECTED:
                    updateSelectedIcon((ImageView) profileIcon);
                    break;
            }
        }




        // Listener per l'icona Home
        homeIcon.setOnClickListener(v -> {
            saveSelectedIconToPrefs(HOME_SELECTED); // Salva la selezione di Home
            updateSelectedIcon((ImageView) v); // Aggiorna l'icona di Home
            context.startActivity(new Intent(context, HomeActivity.class));
        });

        // Listener per l'icona AggiungiTool
        addToolIcon.setOnClickListener(v -> {
            saveSelectedIconToPrefs(ADD_TOOL_SELECTED); // Salva la selezione di AggiungiTool
            updateSelectedIcon((ImageView) v); // Aggiorna l'icona di AggiungiTool
            context.startActivity(new Intent(context, VisualizzaToolActivity.class));
        });

        // Listener per l'icona VisualizzaTool
        viewToolIcon.setOnClickListener(v -> {
            saveSelectedIconToPrefs(VIEW_TOOL_SELECTED); // Salva la selezione di VisualizzaTool
            updateSelectedIcon((ImageView) v); // Aggiorna l'icona di VisualizzaTool
            context.startActivity(new Intent(context, MyGroupActivity.class));
        });

        // Listener per l'icona Profilo
        profileIcon.setOnClickListener(v -> {
            saveSelectedIconToPrefs(PROFILE_SELECTED); // Salva la selezione di Profilo
            updateSelectedIcon((ImageView) v); // Aggiorna l'icona di Profilo
            context.startActivity(new Intent(context, ProfileActivity.class));
        });
    }

    // Metodo per aggiornare l'icona selezionata basata sulla View passata
    private void updateSelectedIcon(ImageView selectedIcon) {
        // Rimuove la selezione da tutte le icone
        resetIcons();

        // Imposta l'icona selezionata
        if (selectedIcon == homeIcon) {
            homeIcon.setImageResource(R.drawable.ic_home_selected); // Home rossa
        } else if (selectedIcon == addToolIcon) {
            addToolIcon.setImageResource(R.drawable.ic_tool_selected); // Tool rosso
        } else if (selectedIcon == viewToolIcon) {
            viewToolIcon.setImageResource(R.drawable.ic_group_selected); // Group rosso
        } else if (selectedIcon == profileIcon) {
            profileIcon.setImageResource(R.drawable.ic_profile_selected); // Profile rosso
        }
    }

    // Metodo per resettare tutte le icone
    private void resetIcons() {
        homeIcon.setImageResource(R.drawable.ic_home); // Home nera
        addToolIcon.setImageResource(R.drawable.ic_tool); // Tool nero
        viewToolIcon.setImageResource(R.drawable.ic_group); // Group nero
        profileIcon.setImageResource(R.drawable.ic_profile); // Profile nero
    }

    // Metodo per salvare lo stato della selezione dell'icona nelle SharedPreferences
    private void saveSelectedIconToPrefs(String selectedIcon) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_SELECTED_ICON, selectedIcon);
        editor.apply();
    }

    // Metodo per recuperare lo stato della selezione dell'icona dalle SharedPreferences
    private String getSelectedIconFromPrefs() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(PREF_SELECTED_ICON, HOME_SELECTED); // Default è Home
    }

    //Metodo per ripristinare l'icona predefinita al termine dell'app
    public void resetToDefaultOnAppClose() {
        saveSelectedIconToPrefs(HOME_SELECTED); // Imposta Home come predefinito
    }
}

