package com.ddrum.superchatvippro.view.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ddrum.superchatvippro.adapter.PeopleAdapter;
import com.ddrum.superchatvippro.R;
import com.ddrum.superchatvippro.constant.Constant;
import com.ddrum.superchatvippro.model.User;
import com.ddrum.superchatvippro.view.activity.MainViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class PeopleFragment extends Fragment {

    private AppCompatEditText edtSearch;
    private RecyclerView rcvSearch;

    private PeopleAdapter adapter;

    private FirebaseUser currentUser;
    private DatabaseReference reference;

    private MainViewModel viewModel;

    public static PeopleFragment newInstance() {

        Bundle args = new Bundle();

        PeopleFragment fragment = new PeopleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_people, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        initView(view);
        intDatabase();

        Query query = reference.child(Constant.USER);
        adapter = new PeopleAdapter(requireContext(), viewModel, query);
        rcvSearch.setAdapter(adapter);
        rcvSearch.setLayoutManager(new LinearLayoutManager(requireContext()));

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Query trường tên (username)
                Query query = reference.child(Constant.USER).orderByChild("username").startAt(s.toString()).endAt(s + "\uf8ff");
                adapter = new PeopleAdapter(requireContext(), viewModel, query);
                rcvSearch.setAdapter(adapter);
                addFriendClick();
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        addFriendClick();
    }

    private void addFriendClick(){
        adapter.setOnAddFriendClick(new PeopleAdapter.Callback() {
            @Override
            public void onClick(int position, User user, boolean isSent) {
                String currentUserId = currentUser.getUid();
                String otherUserId = user.getId();
                reference.child(Constant.RECEIVER).child(otherUserId).child(currentUserId)
                        .setValue(isSent ? currentUserId : null);
                reference.child(Constant.SENDER).child(currentUserId).child(otherUserId)
                        .setValue(isSent ? otherUserId : null);
            }
        });
    }
    private void intDatabase() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
    }
    private void initView(View view) {
        edtSearch = view.findViewById(R.id.edtSearch);
        rcvSearch = view.findViewById(R.id.rcvSearch);
    }
}
