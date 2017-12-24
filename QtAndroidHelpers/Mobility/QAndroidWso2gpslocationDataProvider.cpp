/*
	Offscreen Android Views library for Qt

    Authors:
    Uladzislau Vasilyeu <vasvlad@gmail.com>

    Distrbuted under The BSD License

    Copyright (c) 2017.

	Redistribution and use in source and binary forms, with or without
	modification, are permitted provided that the following conditions are met:

	* Redistributions of source code must retain the above copyright notice,
	  this list of conditions and the following disclaimer.
	* Redistributions in binary form must reproduce the above copyright notice,
	  this list of conditions and the following disclaimer in the documentation
	  and/or other materials provided with the distribution.
	* Neither the name of the DoubleGIS, LLC nor the names of its contributors
	  may be used to endorse or promote products derived from this software
	  without specific prior written permission.

	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
	AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
	THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
	ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS
	BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
	CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
	SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
	INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
	CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
	ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
	THE POSSIBILITY OF SUCH DAMAGE.
*/

#include "QAndroidWso2gpslocationDataProvider.h"
#include <QAndroidQPAPluginGap.h>
#include "TJniObjectLinker.h"


namespace Mobility {

Q_DECL_EXPORT void JNICALL Java_Wso2gpslocationListener_locationInfoUpdate(JNIEnv *, jobject, jlong native_ptr, jint time, jdouble lat, jdouble lon, jdouble altitude, jfloat bearing, jfloat speed)
{
	JNI_LINKER_OBJECT(Mobility::QAndroidWso2gpslocationDataProvider, native_ptr, proxy)
	proxy->locationInfo(time, lat, lon, altitude, bearing, speed);
}

static const JNINativeMethod methods[] = {
	{"getContext", "()Landroid/content/Context;", (void*)QAndroidQPAPluginGap::getCurrentContext},
	{"locationInfoUpdate", "(JIDDDFF)V", (void*)Java_Wso2gpslocationListener_locationInfoUpdate},
};

JNI_LINKER_IMPL(QAndroidWso2gpslocationDataProvider, "ru/dublgis/androidhelpers/mobility/Wso2gpslocationListener", methods)

QAndroidWso2gpslocationDataProvider::QAndroidWso2gpslocationDataProvider(QObject * parent)
	: QObject(parent)
	, jniLinker_(new JniObjectLinker(this))
{
}

QAndroidWso2gpslocationDataProvider::~QAndroidWso2gpslocationDataProvider()
{
}

void QAndroidWso2gpslocationDataProvider::start()
{
	if (isJniReady())
	{
		jni()->callBool("start");
	}
}

void QAndroidWso2gpslocationDataProvider::stop()
{
	if (isJniReady())
	{
		jni()->callVoid("stop");
	}
}

void QAndroidWso2gpslocationDataProvider::locationInfo(int time, double lat, double lon, double altitude, float bearing, float speed)
{
    emit locationInfoUpdate(time, lat, lon, altitude, bearing, speed);
}

}
