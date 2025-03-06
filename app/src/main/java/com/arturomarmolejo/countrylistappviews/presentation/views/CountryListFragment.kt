package com.arturomarmolejo.countrylistappviews.presentation.views

import android.net.Network
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arturomarmolejo.countrylistappviews.R
import com.arturomarmolejo.countrylistappviews.core.UIState
import com.arturomarmolejo.countrylistappviews.data.network.NetworkModule
import com.arturomarmolejo.countrylistappviews.data.repository.CountryRepository
import com.arturomarmolejo.countrylistappviews.data.repository.impl.CountryRepositoryImpl
import com.arturomarmolejo.countrylistappviews.databinding.FragmentCountryListBinding
import com.arturomarmolejo.countrylistappviews.domain.GetAllCountriesUseCase
import com.arturomarmolejo.countrylistappviews.presentation.viewmodel.CountryViewModel
import com.arturomarmolejo.countrylistappviews.presentation.views.recyclerview.adapter.CountryListAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CountryListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CountryListFragment : Fragment() {

    private var _binding: FragmentCountryListBinding? = null
    private val binding get() = _binding!!

    private val recyclerView by lazy {
        binding.countryListRecyclerview
    }
    private var scrollPosition: Parcelable? = null

    private val countryViewModel: CountryViewModel by lazy {
        val service = NetworkModule.providesCountryServiceApi()
        val dispatcher = NetworkModule.providesCoroutineDispatcher()
        val repository: CountryRepository = CountryRepositoryImpl(service)
        val useCase: GetAllCountriesUseCase = GetAllCountriesUseCase(repository, dispatcher)
        CountryViewModel(useCase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCountryListBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listAdapter = CountryListAdapter()
        recyclerView.adapter = listAdapter

        // Set layout manager
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        //Set state restoration policy to save scroll position and make it survive through configuration changes
        listAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        listAdapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                if (scrollPosition != null) {
                    recyclerView.layoutManager?.onRestoreInstanceState(scrollPosition)
                    scrollPosition = null
                }
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            //Observation of flow data stream from the ViewModel. Notice use of repeatOnLifecycle
            //to make the flow only stream as long as the fragment is active.
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                countryViewModel.allCountries.collect { state ->
                    when(state) {
                        is UIState.LOADING -> {
                            // Loading spinner to be visible when we receive the first state
                            binding.loadingSpinner.visibility = View.VISIBLE
                        }
                        is UIState.SUCCESS -> {
                            binding.loadingSpinner.visibility = View.GONE
                            //Submit list to the adapter when we receive a success state
                            listAdapter.submitList(state.response)
                        }
                        is UIState.ERROR -> {
                            binding.loadingSpinner.visibility = View.GONE
                            //Toast to be shown when we receive an error state
                            Toast.makeText(requireContext(), state.message.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        //Listener for the scroll event to save the scroll position from recycler view
        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                scrollPosition = recyclerView.layoutManager?.onSaveInstanceState()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scrollPosition = binding.countryListRecyclerview.layoutManager?.onSaveInstanceState()
        _binding = null
    }

}