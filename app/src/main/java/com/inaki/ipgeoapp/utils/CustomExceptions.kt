package com.inaki.ipgeoapp.utils

class NullBodyException(message: String? = "Null body in response") : Exception(message)
class FailResponseException(message: String?) : Exception(message)
class NoNetworkAvailableException(message: String? = "No internet connection available") : Exception(message)
class NoIpAddressException(message: String? = "No IP address entered") : Exception(message)
class InvalidIpAddressException(message: String? = "The IP address is invalid format") : Exception(message)