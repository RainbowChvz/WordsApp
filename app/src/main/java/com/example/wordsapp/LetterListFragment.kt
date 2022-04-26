package com.example.wordsapp

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wordsapp.data.SettingsDataStore
import com.example.wordsapp.databinding.FragmentLetterListBinding
import kotlinx.coroutines.launch

class LetterListFragment : Fragment() {

    private var _binding: FragmentLetterListBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var settingsDataStore: SettingsDataStore
    private var isLinearLayoutManager = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        return super.onCreateView(inflater, container, savedInstanceState)

        _binding = FragmentLetterListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.recyclerView

        settingsDataStore = SettingsDataStore(requireContext())
        settingsDataStore.preferenceFlow.asLiveData()
            .observe(viewLifecycleOwner) { value ->
                isLinearLayoutManager = value
                chooseLayout()

                // Invalidate options menu in order to be redrawn
                activity?.invalidateOptionsMenu()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.layout_menu, menu)
        val layoutButton = menu.findItem(R.id.action_switch_layout)
        setIcon(layoutButton)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_switch_layout -> {
                isLinearLayoutManager = !isLinearLayoutManager
                chooseLayout()
                setIcon(item)

                lifecycleScope.launch {
                    settingsDataStore.saveLayoutToPreferencesStore(
                        isLinearLayoutManager,
                        requireContext()
                    )
                }

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun chooseLayout() {
        recyclerView.layoutManager =
            if (isLinearLayoutManager)
                LinearLayoutManager(context)
            else
                GridLayoutManager(context, 4)

        recyclerView.adapter = LetterAdapter()
    }

    private fun setIcon(menuItem: MenuItem?) {
        if (menuItem == null)
            return

        menuItem.icon =
            if (!isLinearLayoutManager)
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_linear_layout)
            else
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_grid_layout)

    }
}