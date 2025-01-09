package group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tooltrip.R;

import java.util.List;

public class MemberAdapter extends ArrayAdapter<Member> {

    private Context context;
    private List<Member> members;

    public MemberAdapter(Context context, List<Member> members) {
        super(context, R.layout.member_list_item, members);
        this.context = context;
        this.members = members;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Member member = members.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.member_list_item, parent, false);
        }

        ImageView imageViewProfile = convertView.findViewById(R.id.imageViewProfile);
        TextView textViewMemberName = convertView.findViewById(R.id.textViewMemberName);

        textViewMemberName.setText(member.getName());

        // Load profile image using an image loading library like Picasso or Glide
        // For example, using Glide:
        Glide.with(context)
                .load(member.getProfileImageUrl())
                .placeholder(R.drawable.ic_profile)
                .into(imageViewProfile);

        return convertView;
    }
}

