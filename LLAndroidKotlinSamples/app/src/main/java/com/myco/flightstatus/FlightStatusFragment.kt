package com.myco.flightstatus

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.locuslabs.sdk.llpublic.*
import com.myco.MainViewModel
import com.myco.R
import java.text.SimpleDateFormat
import java.util.*


class FlightStatusFragment: DialogFragment() {
    private val mainViewModel by activityViewModels<MainViewModel>()

    private lateinit var icon_image_view: ImageView
    private lateinit var gate_text_view: TextView
    private lateinit var section_text_view: TextView
    private lateinit var floor_text_view: TextView

    private lateinit var gate_type_text_view: TextView
    private lateinit var flight_status_view_flight_number_text: TextView
    private lateinit var flight_status_view_starting_venue_text: TextView
    private lateinit var flight_status_view_starting_time: TextView
    private lateinit var flight_status_view_starting_time_estimated: TextView
    private lateinit var current_gate_baggage_claim_text_view: TextView
    private lateinit var ending_venue_text_view: TextView
    private lateinit var ending_time_text_view: TextView
    private lateinit var estimated_ending_time_text_view: TextView
    private lateinit var next_gate_baggage_claim_text_view: TextView
    private lateinit var updated_information_text_view: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fs_flight_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUIObservers()

        initIDs()
    }

    private fun initIDs() {
        icon_image_view = requireView().findViewById(R.id.fs_flight_view_plane_pin)
        gate_text_view = requireView().findViewById(R.id.fs_flight_view_gate_text)
        section_text_view = requireView().findViewById(R.id.fs_flight_view_section_text)
        floor_text_view = requireView().findViewById(R.id.fs_flight_view_floor_text)
        gate_type_text_view = requireView().findViewById(R.id.fs_flight_view_gate_type_text)
        flight_status_view_flight_number_text =
            requireView().findViewById(R.id.flight_status_view_flight_number_text)
        flight_status_view_starting_venue_text =
            requireView().findViewById(R.id.flight_status_view_starting_venue_text)
        flight_status_view_starting_time =
            requireView().findViewById(R.id.flight_status_view_starting_time)
        flight_status_view_starting_time_estimated =
            requireView().findViewById(R.id.flight_status_view_starting_time_estimated)
        current_gate_baggage_claim_text_view =
            requireView().findViewById(R.id.flight_status_view_current_gate_baggage_claim)
        ending_venue_text_view =
            requireView().findViewById(R.id.flight_status_view_ending_venue_text)
        ending_time_text_view = requireView().findViewById(R.id.flight_status_view_ending_time)
        estimated_ending_time_text_view =
            requireView().findViewById(R.id.flight_status_view_ending_time_estimated)
        next_gate_baggage_claim_text_view =
            requireView().findViewById(R.id.flight_status_view_next_gate_baggage_claim)
        updated_information_text_view =
            requireView().findViewById(R.id.flight_status_view_updated_information)

    }

    private fun initUIObservers() {
        mainViewModel.showFlightStatusFragment.observe(
            viewLifecycleOwner, {
                if (it) {
                    LLPOIDatabase().getPOIDetails(
                        mainViewModel.venueID,
                        mainViewModel.poiID,
                        object : LLOnGetPOIDetailsCallback {
                            override fun successCallback(poi: LLPOI) {
                                setFlightStatusViewText(poi, mainViewModel.flight)
                            }

                            override fun failureCallback(throwable: Throwable) {
                                TODO("Not yet implemented")
                            }
                        })
                }

                requireView().visibility = if (it) View.VISIBLE else View.GONE
            }
        )
    }

    //===============================//
    //  Implement Overriding Methods //
    //===============================//
    private fun setFlightStatusViewText(poi: LLPOI, flight: Flight) {

        //================//
        //    Variables   //
        //================//

        // Primitive
        var scheduledArrivalTime = ""
        var scheduledDepartureTime = ""
        var estimatedArrivalTime = ""
        var estimatedDepartureTime = ""

        //Boolean Determinant Factor
        val isArrivalGate = isArrivalGate(requireContext(), poi, flight)
        val isDepartingGate = isDepartureGate(requireContext(), poi, flight)

        // LL Class Objects
        val level: LLLevel = poi.level

        // Derived POI Strings
        val poiName: String = poi.name
        val floorDescription: String = level.name

        // Derived Flight Strings
        val arrivalVenue: String =
            flight.arrivalGate.airportName.toString() + " ( " + flight.arrivalGate
                .airportCode.toUpperCase(Locale.ROOT) + " )"
        val departureVenue: String = flight.departureGate.airportName
            .toString() + " ( " + flight.departureGate.airportCode.toUpperCase(Locale.ROOT) + " )"
        val operatingFlight: String = flight.operatingFlightCode.airline.name
            .toString() + " " + getString(R.string.fs_common_flight) + " " + flight.operatingFlightCode
            .number
        val hasArrivalGateBaggageClaim =
            if (flight.arrivalGate.baggageClaim != null) flight.arrivalGate
                .baggageClaim else getString(R.string.fs_common_unavailable)
        val hasDepartureGateBaggageClaim =
            if (flight.departureGate.baggageClaim != null) flight.departureGate
                .baggageClaim else getString(R.string.fs_common_unavailable)

        // Derived Date-Time Strings
        val dateFormatPattern = SimpleDateFormat("hh:mma", Locale.getDefault())

        // Arrival Sched.
        if (flight.arrivalTimes != null && flight.arrivalTimes!!.scheduled != null) {
            scheduledArrivalTime =
                getString(R.string.arrival_sched) + " - " + dateFormatPattern.format(
                    flight.arrivalTimes!!.scheduled!!
                ).replace("AM", "a").replace("PM", "p")
            if (isArrivalGate) {
                flight_status_view_starting_time.visibility = View.VISIBLE
                flight_status_view_starting_time.layoutParams.height =
                    LinearLayout.LayoutParams.WRAP_CONTENT
                flight_status_view_starting_time.layoutParams =
                    flight_status_view_starting_time.layoutParams
            } else {
                ending_time_text_view.visibility = View.VISIBLE
                ending_time_text_view.layoutParams.height =
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ending_time_text_view.layoutParams = ending_time_text_view.layoutParams
            }
        } else {
            if (isArrivalGate) {
                flight_status_view_starting_time.visibility = View.GONE
                flight_status_view_starting_time.layoutParams.height = 0
                flight_status_view_starting_time.layoutParams =
                    flight_status_view_starting_time.layoutParams
            } else {
                ending_time_text_view.visibility = View.GONE
                ending_time_text_view.layoutParams.height = 0
                ending_time_text_view.layoutParams = ending_time_text_view.layoutParams
            }
        }

        // Arrival Est.
        if (flight.arrivalTimes != null && flight.arrivalTimes!!.estimated != null) {
            estimatedArrivalTime =
                getString(R.string.arrival_est) + " - " + dateFormatPattern.format(
                    flight.arrivalTimes!!.estimated!!
                ).replace("AM", "a").replace("PM", "p")
            if (isArrivalGate) {
                flight_status_view_starting_time_estimated.visibility = View.VISIBLE
                flight_status_view_starting_time_estimated.layoutParams.height =
                    LinearLayout.LayoutParams.WRAP_CONTENT
                flight_status_view_starting_time_estimated.layoutParams =
                    flight_status_view_starting_time_estimated.layoutParams
            } else {
                estimated_ending_time_text_view.visibility = View.VISIBLE
                estimated_ending_time_text_view.layoutParams.height =
                    LinearLayout.LayoutParams.WRAP_CONTENT
                estimated_ending_time_text_view.layoutParams =
                    estimated_ending_time_text_view.layoutParams
            }
        } else {
            if (isArrivalGate) {
                flight_status_view_starting_time_estimated.visibility = View.GONE
                flight_status_view_starting_time_estimated.layoutParams.height = 0
                flight_status_view_starting_time_estimated.layoutParams =
                    flight_status_view_starting_time_estimated.layoutParams
            } else {
                estimated_ending_time_text_view.visibility = View.GONE
                estimated_ending_time_text_view.layoutParams.height = 0
                estimated_ending_time_text_view.layoutParams =
                    estimated_ending_time_text_view.layoutParams
            }
        }

        // Departure Sched.
        if (flight.departureTimes != null && flight.departureTimes!!
                .scheduled != null
        ) {
            scheduledDepartureTime =
                getString(R.string.departure_sched) + " - " + dateFormatPattern.format(
                    flight.departureTimes!!.scheduled!!
                ).replace("AM", "a").replace("PM", "p")
            if (isDepartingGate) {
                flight_status_view_starting_time.visibility = View.VISIBLE
                flight_status_view_starting_time.layoutParams.height =
                    LinearLayout.LayoutParams.WRAP_CONTENT
                flight_status_view_starting_time.layoutParams =
                    flight_status_view_starting_time.layoutParams
            } else {
                ending_time_text_view.visibility = View.VISIBLE
                ending_time_text_view.layoutParams.height =
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ending_time_text_view.layoutParams = ending_time_text_view.layoutParams
            }
        } else {
            if (isDepartingGate) {
                flight_status_view_starting_time.visibility = View.GONE
                flight_status_view_starting_time.layoutParams.height = 0
                flight_status_view_starting_time.layoutParams =
                    flight_status_view_starting_time.layoutParams
            } else {
                ending_time_text_view.visibility = View.GONE
                ending_time_text_view.layoutParams.height = 0
                ending_time_text_view.layoutParams = ending_time_text_view.layoutParams
            }
        }

        // Departure Est.
        if (flight.departureTimes != null && flight.departureTimes!!
                .estimated != null
        ) {
            estimatedDepartureTime =
                getString(R.string.departure_est) + " - " + dateFormatPattern.format(
                    flight.departureTimes!!.estimated!!
                ).replace("AM", "a").replace("PM", "p")
            if (isDepartingGate) {
                flight_status_view_starting_time_estimated.visibility = View.VISIBLE
                flight_status_view_starting_time_estimated.layoutParams.height =
                    LinearLayout.LayoutParams.WRAP_CONTENT
                flight_status_view_starting_time_estimated.layoutParams =
                    flight_status_view_starting_time_estimated.layoutParams
            } else {
                estimated_ending_time_text_view.visibility = View.VISIBLE
                estimated_ending_time_text_view.layoutParams.height =
                    LinearLayout.LayoutParams.WRAP_CONTENT
                estimated_ending_time_text_view.layoutParams =
                    estimated_ending_time_text_view.layoutParams
            }
        } else {
            if (isDepartingGate) {
                flight_status_view_starting_time_estimated.visibility = View.GONE
                flight_status_view_starting_time_estimated.layoutParams.height = 0
                flight_status_view_starting_time_estimated.layoutParams =
                    flight_status_view_starting_time_estimated.layoutParams
            } else {
                estimated_ending_time_text_view.visibility = View.GONE
                estimated_ending_time_text_view.layoutParams.height = 0
                estimated_ending_time_text_view.layoutParams =
                    estimated_ending_time_text_view.layoutParams
            }
        }

        //=================//
        // Implementations //
        //=================//

        // Flight Status View Location Section

        // Header Pin Icon
        icon_image_view.setImageResource(if (isArrivalGate) R.drawable.fs_pin_plane_landing else R.drawable.fs_pin_plane_takeoff)

        // Gate Text
        gate_text_view.text = poiName

        // Section Text
        section_text_view.text = poi.level.building.name

        // Floor Text
        floor_text_view.text = floorDescription

        // Flight Status View Starting Location - Information Section

        // Airline Logo
        val logoInputStream = requireContext().assets.open(flight.operatingFlightCode.airline.smallIconUrl)
        val logoDrawable = Drawable.createFromStream(logoInputStream, null)
        val airline_logo_image_view =
            requireView().findViewById<View>(R.id.fs_flight_view_airline_logo) as ImageView
        airline_logo_image_view.setImageDrawable(logoDrawable)

        // Gate Type
        gate_type_text_view.text =
            if (isArrivalGate) getString(R.string.arrival_gate) else if (isDepartingGate) getString(
                R.string.departure_gate
            ) else ""

        // Operating Flight
        flight_status_view_flight_number_text.text = operatingFlight

        // Current Gate's Venue Name
        flight_status_view_starting_venue_text.text =
            if (isArrivalGate) arrivalVenue else if (isDepartingGate) departureVenue else arrivalVenue

        // Current Gate's Scheduled Time
        flight_status_view_starting_time.text =
            if (isArrivalGate) scheduledArrivalTime else if (isDepartingGate) scheduledDepartureTime else scheduledArrivalTime

        // Current Gate's Estimated Time
        flight_status_view_starting_time_estimated.text =
            if (isArrivalGate) estimatedArrivalTime else if (isDepartingGate) estimatedDepartureTime else estimatedArrivalTime

        //Current Gate's associated Baggage Claim
        current_gate_baggage_claim_text_view.text =
            if (isArrivalGate) hasArrivalGateBaggageClaim else if (isDepartingGate) hasDepartureGateBaggageClaim else hasArrivalGateBaggageClaim

        // Flight Status View Ending Location - Information Section

        // Next Gate's Venue Name
        ending_venue_text_view.text =
            if (isArrivalGate) departureVenue else if (isDepartingGate) arrivalVenue else departureVenue

        // Next Gate's Scheduled Time
        ending_time_text_view.text =
            if (isArrivalGate) scheduledDepartureTime else if (isDepartingGate) scheduledArrivalTime else scheduledDepartureTime

        // Next Gate's Estimated Time
        estimated_ending_time_text_view.text =
            if (isArrivalGate) estimatedDepartureTime else if (isDepartingGate) estimatedArrivalTime else estimatedDepartureTime

        //Next Gate's  associated Baggage Claim
        next_gate_baggage_claim_text_view.text =
            if (isArrivalGate) hasDepartureGateBaggageClaim else if (isDepartingGate) hasArrivalGateBaggageClaim else hasDepartureGateBaggageClaim

        // Flight Status information's recency
        updated_information_text_view.text = ""
    }
}