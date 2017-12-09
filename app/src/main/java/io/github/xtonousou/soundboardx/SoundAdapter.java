package io.github.xtonousou.soundboardx;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.Normalizer;
import java.util.ArrayList;

public class SoundAdapter extends RecyclerView.Adapter<SoundAdapter.ViewHolder>
        implements Filterable {
    private static final String TAG = "SoundAdapter";

    private Activity activity;
    private Typeface font;
    private ArrayList<Sound> sounds;
    private ArrayList<Sound> soundsCopy;

    private boolean animationsShown;
    private boolean favoritesOnly      = false;
    private boolean allSoundsOnly      = false;
    private boolean funnySoundsOnly    = false;
    private boolean gamesSoundsOnly    = false;
    private boolean moviesSoundsOnly   = false;

    SoundAdapter(Activity activity, ArrayList<Sound> soundArray, boolean withAnimations) {
        this.activity        = activity;
        this.font            = Typeface.createFromAsset(activity.getAssets(),
				"fonts/Roboto-Regular.ttf");
        this.sounds          = soundArray;
        this.soundsCopy      = soundArray;
        this.animationsShown = withAnimations;
    }

    boolean areAnimationsShown() {
        return animationsShown;
    }

    boolean isFavoritesOnly() {
        return favoritesOnly;
    }

    void setShowAnimations(boolean anim) {
        animationsShown = anim;
    }

    void onlyShowFavorites() {
        favoritesOnly = true;
        for (Sound sound : new ArrayList<>(sounds)) {
            if (!sound.getFavorite()) {
                notifyItemRemoved(sounds.indexOf(sound));
                sounds.remove(sound);
            }
        }
    }

    void showAllSounds(Context context) {
        favoritesOnly = false;
        allSoundsOnly = true;
        funnySoundsOnly = false;
        gamesSoundsOnly = false;
        moviesSoundsOnly = false;

        sounds = SoundStore.getAllSounds(context);
        notifyDataSetChanged();
    }

    void showFunnySounds(Context context) {
        favoritesOnly = false;
        allSoundsOnly = false;
        funnySoundsOnly = true;
        gamesSoundsOnly = false;
        moviesSoundsOnly = false;

        sounds = SoundStore.getFunnySounds(context);
        notifyDataSetChanged();
    }

    void showGamesSounds(Context context) {
        favoritesOnly = false;
        allSoundsOnly = false;
        funnySoundsOnly = false;
        gamesSoundsOnly = true;
        moviesSoundsOnly = false;

        sounds = SoundStore.getGamesSounds(context);
        notifyDataSetChanged();
    }

    void showMoviesSounds(Context context) {
        favoritesOnly = false;
        allSoundsOnly = false;
        funnySoundsOnly = false;
        gamesSoundsOnly = false;
        moviesSoundsOnly = true;

        sounds = SoundStore.getMoviesSounds(context);
        notifyDataSetChanged();
    }

    /**
     *  returns  0 if allSoundsOnly,
     *  returns  1 if funnySoundsOnly,
     *  returns  2 if gamesSoundsOnly,
     *  returns  3 if moviesSoundsOnly,
     *  returns -1 if unexpected occurrence.
     */
    byte getCategory() {
        byte category;
        if (allSoundsOnly) {
            category = 0;
        } else if (funnySoundsOnly) {
            category =  1;
        } else if (gamesSoundsOnly) {
            category =  2;
        } else if (moviesSoundsOnly) {
            category =  3;
        } else {
            category = -1;
        }
        return category;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnCreateContextMenuListener,
            MenuItem.OnMenuItemClickListener {
        public final TextView title;
        final ImageButton favButton;

        ViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.title);
            favButton = v.findViewById(R.id.fav_button);

            title.setTypeface(font);

            itemView.setOnClickListener(new View.OnClickListener() {
                public void onEvent(String event) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        if (EventBus.getDefault().isRegistered(this)) {
                            EventBus.getDefault().unregister(this);
                        }
                        notifyItemChanged(getAdapterPosition());
                    }
                }

                @Override
				@Subscribe(threadMode = ThreadMode.POSTING)
                public void onClick(View view) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        if (EventBus.getDefault().isRegistered(this)) return;
                        if (animationsShown)
							new ParticleManager(new Particle(itemView), title.getText().toString())
									.makeItShine();
                        EventBus.getDefault().register(this);
                        EventBus.getDefault().post(sounds.get(getAdapterPosition()));
                        EventBus.getDefault().unregister(this);
                    }
                }
            });

            v.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(final ContextMenu contextMenu, View view,
                                        ContextMenu.ContextMenuInfo contextMenuInfo) {

            contextMenu.setHeaderTitle(R.string.header);
            contextMenu.setHeaderIcon(
                    new IconicsDrawable(view.getContext())
                            .icon(FontAwesome.Icon.faw_music)
                            .color(Color.WHITE)
                            .sizeDp(24)
            );

            MenuItem setRingtone = contextMenu.add(R.string.ringtone);
            MenuItem setNotification = contextMenu.add(R.string.notification);
            MenuItem setAlarm = contextMenu.add(R.string.alarm);

            setRingtone.setOnMenuItemClickListener(this);
            setNotification.setOnMenuItemClickListener(this);
            setAlarm.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            ToneManager tone = new ToneManager(activity, itemView, title.getText().toString(),
					getAdapterPosition());
            switch (menuItem.getTitle().toString()) {
                default:
                    Log.e(TAG, "onMenuItemClick: menuItem.getTitle().toString()");
                case "Set as ringtone":
					tone.setToneAs((byte) 0);
                    break;
                case "Set as notification":
					tone.setToneAs((byte) 1);
                    break;
                case "Set as alarm":
					tone.setToneAs((byte) 2);
                    break;
            }
            return true;
        }
    }

    @Override
    public SoundAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sound_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.title.setText(sounds.get(position).getName());
        Utils.paintThis(holder);

        boolean isFavorite = sounds.get(holder.getAdapterPosition()).getFavorite();

        holder.favButton.setImageDrawable(isFavorite
                ?   new IconicsDrawable(holder.favButton.getContext()).icon(FontAwesome.Icon.faw_heart)
                .color(Color.WHITE)
                .sizeDp(24)
                :   new IconicsDrawable(holder.favButton.getContext())
                .icon(FontAwesome.Icon.faw_heart_o)
                .color(Color.WHITE)
                .sizeDp(24));

        holder.favButton.setOnClickListener(v -> {
            try {
                boolean newFavStatus = !sounds.get(holder.getAdapterPosition()).getFavorite();
                sounds.get(holder.getAdapterPosition()).setFavorite(newFavStatus);

                if (newFavStatus) {
                    ((ImageButton) v).setImageDrawable(
                            new IconicsDrawable(v.getContext())
                                    .icon(FontAwesome.Icon.faw_heart)
                                    .color(Color.WHITE)
                                    .sizeDp(24)
                    );
                    v.setContentDescription(v.getContext().getString(R.string.fav_desc));
                } else {
                    ((ImageButton) v).setImageDrawable(
                            new IconicsDrawable(v.getContext())
                                    .icon(FontAwesome.Icon.faw_heart_o)
                                    .color(Color.WHITE)
                                    .sizeDp(24));
                    v.setContentDescription(v.getContext().getString(R.string.not_fav_desc));
                }

                if (favoritesOnly) {
                    // Remove from the list.
                    sounds.remove(sounds.get(holder.getAdapterPosition()));
                    notifyItemRemoved(holder.getAdapterPosition());
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e(TAG, e.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return sounds != null ? sounds.size() : 0;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private Filter mFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Filter.FilterResults filterResults = new Filter.FilterResults();
            ArrayList<Sound> tempList = new ArrayList<>();
            favoritesOnly = false;
            if ((constraint != null) && (soundsCopy != null)) {
                for (Sound item : new ArrayList<>(soundsCopy)) {
                    String userInput = striptease(constraint.toString().toLowerCase());
                    String itemName = striptease(item.getName().toLowerCase());
                    if (itemName.contains(userInput)) {
                        tempList.add(item);
                    }
                }

                filterResults.values = tempList;
                filterResults.count = tempList.size();
            }
            return filterResults;
        }
        
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            sounds = (ArrayList<Sound>) results.values;
            notifyDataSetChanged();
        }
    };

    private static String striptease(String string) {
        return Normalizer.normalize(string, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll(" ", "")
                .replaceAll("\'", "");
    }
}
