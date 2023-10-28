package com.example.login;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SpecialAdapter extends RecyclerView.Adapter<SpecialAdapter.SpecialViewHolder> {

    private View.OnClickListener onClickListener;
    public static class SpecialViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgDisp;
        private  TextView txtName;
        private TextView txtDesc;
        public Specials specials;
        public SpecialViewHolder(@NonNull View view) {
            super(view);
            txtName = view.findViewById(R.id.txtSpecName);
            txtDesc = view.findViewById(R.id.txtSpecDes);
            imgDisp = view.findViewById(R.id.imgSpecialsCard);

        }

        public void setSepcial(Specials specials) {
            this.specials = specials;

            if(specials.getSpecIMG()==1)
            {
                imgDisp.setImageResource(R.drawable.cocktail);
            }else if(specials.getSpecIMG()==2)
            {
                imgDisp.setImageResource(R.drawable.beer);
            }
            else if (specials.getSpecIMG()==3)
            {
                imgDisp.setImageResource(R.drawable.balloons);
            }
            txtName.setText(specials.getSpecName());
            txtDesc.setText(specials.getSpecDesc());

        }


    }
    private final List<Specials> specials;
    public SpecialAdapter(List<Specials>specials ){
        this.specials= specials;
    }
    @NonNull
    @Override
    public SpecialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.business_specials_adapter, parent, false);
        return new SpecialViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull SpecialViewHolder holder, int position) {
        Specials specialsTemp =specials.get(position);
        holder.setSepcial(specialsTemp);
        holder.itemView.setOnClickListener(onClickListener);
    }
    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public int getItemCount() {
        return specials.size();
    }
}

